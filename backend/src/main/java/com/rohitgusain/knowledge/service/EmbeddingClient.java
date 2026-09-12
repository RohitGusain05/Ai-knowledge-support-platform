package com.rohitgusain.knowledge.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class EmbeddingClient {
    private final RestClient restClient;

    public EmbeddingClient(@Value("${ai-service.url:http://localhost:8000}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public List<List<Double>> embed(List<String> texts) {
        EmbeddingResponse response = restClient.post()
                .uri("/api/v1/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new EmbeddingRequest(texts))
                .retrieve()
                .body(EmbeddingResponse.class);

        if (response == null || response.embeddings() == null || response.embeddings().size() != texts.size()) {
            throw new IllegalStateException("AI service returned an invalid embedding response");
        }
        return response.embeddings();
    }

    private record EmbeddingRequest(List<String> texts) {}
    private record EmbeddingResponse(List<List<Double>> embeddings, int dimension) {}
}
