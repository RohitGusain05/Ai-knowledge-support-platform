package com.rohitgusain.knowledge.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VectorStoreService {
    private final JdbcTemplate jdbcTemplate;

    public VectorStoreService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void storeEmbeddings(List<UUID> chunkIds, List<List<Double>> embeddings) {
        if (chunkIds.size() != embeddings.size()) throw new IllegalArgumentException("Chunk and embedding counts must match");
        for (int i = 0; i < chunkIds.size(); i++) {
            List<Double> vector = embeddings.get(i);
            if (vector.size() != 384) throw new IllegalArgumentException("Expected 384-dimensional embeddings");
            jdbcTemplate.update("UPDATE document_chunks SET embedding = CAST(? AS vector) WHERE id = ?", toPgVector(vector), chunkIds.get(i));
        }
    }

    public List<RetrievedChunk> search(UUID spaceId, List<Double> queryEmbedding, int limit) {
        if (queryEmbedding.size() != 384) throw new IllegalArgumentException("Expected 384-dimensional query embedding");
        String vector = toPgVector(queryEmbedding);
        return jdbcTemplate.query(
                "SELECT dc.id, dc.document_id, dc.chunk_index, dc.content, " +
                "1 - (dc.embedding <=> CAST(? AS vector)) AS similarity " +
                "FROM document_chunks dc JOIN documents d ON d.id = dc.document_id " +
                "WHERE d.knowledge_space_id = ? AND dc.embedding IS NOT NULL " +
                "ORDER BY dc.embedding <=> CAST(? AS vector) LIMIT ?",
                (rs, rowNum) -> new RetrievedChunk(rs.getObject("id", UUID.class), rs.getObject("document_id", UUID.class),
                        rs.getInt("chunk_index"), rs.getString("content"), rs.getDouble("similarity")),
                vector, spaceId, vector, limit);
    }

    private String toPgVector(List<Double> vector) {
        return "[" + vector.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(",")) + "]";
    }

    public record RetrievedChunk(UUID id, UUID documentId, int chunkIndex, String content, double similarity) {}
}
