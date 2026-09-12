package com.rohitgusain.knowledge.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class AnswerGenerationClient {
    private final RestClient restClient;

    public AnswerGenerationClient(@Value("${ai-service.url:http://localhost:8000}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public String generate(String question, List<VectorStoreService.RetrievedChunk> chunks) {
        String context = chunks.stream()
                .map(c -> "[Document " + c.documentId() + ", chunk " + c.chunkIndex() + "]\n" + c.content())
                .reduce("", (a, b) -> a + "\n\n" + b);
        AnswerResponse response = restClient.post()
                .uri("/api/v1/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AnswerRequest(question, context))
                .retrieve()
                .body(AnswerResponse.class);
        if (response == null || response.answer() == null || response.answer().isBlank()) {
            throw new IllegalStateException("AI service returned an empty answer");
        }
        return response.answer();
    }

    private record AnswerRequest(String question, String context) {}
    private record AnswerResponse(String answer) {}
}
