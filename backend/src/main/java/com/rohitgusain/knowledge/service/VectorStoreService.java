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
        if (chunkIds.size() != embeddings.size()) {
            throw new IllegalArgumentException("Chunk and embedding counts must match");
        }
        for (int i = 0; i < chunkIds.size(); i++) {
            List<Double> vector = embeddings.get(i);
            if (vector.size() != 384) {
                throw new IllegalArgumentException("Expected 384-dimensional embeddings");
            }
            jdbcTemplate.update(
                    "UPDATE document_chunks SET embedding = CAST(? AS vector) WHERE id = ?",
                    toPgVector(vector), chunkIds.get(i)
            );
        }
    }

    private String toPgVector(List<Double> vector) {
        return "[" + vector.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(",")) + "]";
    }
}
