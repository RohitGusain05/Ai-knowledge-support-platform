package com.rohitgusain.knowledge.controller;

import com.rohitgusain.knowledge.service.QuestionAnswerService;
import com.rohitgusain.knowledge.service.VectorStoreService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/knowledge-spaces/{spaceId}/questions")
public class QuestionController {
    private final QuestionAnswerService service;

    public QuestionController(QuestionAnswerService service) { this.service = service; }

    @PostMapping
    public AnswerResponse ask(@PathVariable UUID spaceId, @RequestBody QuestionRequest request,
                              Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        var result = service.answer(userId, spaceId, request.question(), request.limitOrDefault());
        return new AnswerResponse(result.answer(), result.sources().stream().map(Source::from).toList());
    }

    public record QuestionRequest(String question, Integer limit) {
        int limitOrDefault() { return limit == null ? 5 : limit; }
    }
    public record AnswerResponse(String answer, List<Source> sources) {}
    public record Source(UUID chunkId, UUID documentId, int chunkIndex, String content, double similarity) {
        static Source from(VectorStoreService.RetrievedChunk c) {
            return new Source(c.id(), c.documentId(), c.chunkIndex(), c.content(), c.similarity());
        }
    }
}
