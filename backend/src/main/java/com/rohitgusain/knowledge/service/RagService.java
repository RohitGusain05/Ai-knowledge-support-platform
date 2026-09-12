package com.rohitgusain.knowledge.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RagService {
    private final AiEmbeddingClient embeddingClient;
    private final VectorStoreService vectorStoreService;

    public RagService(AiEmbeddingClient embeddingClient, VectorStoreService vectorStoreService) {
        this.embeddingClient = embeddingClient;
        this.vectorStoreService = vectorStoreService;
    }

    public List<VectorStoreService.RetrievedChunk> retrieve(UUID spaceId, String question, int limit) {
        if (question == null || question.isBlank()) throw new IllegalArgumentException("Question is required");
        int safeLimit = Math.max(1, Math.min(limit, 10));
        List<List<Double>> embeddings = embeddingClient.embed(List.of(question));
        return vectorStoreService.search(spaceId, embeddings.get(0), safeLimit);
    }
}
