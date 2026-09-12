package com.rohitgusain.knowledge.controller;

import com.rohitgusain.knowledge.service.QuestionAnswerService;
import com.rohitgusain.knowledge.service.VectorStoreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    public AnswerResponse ask(@PathVariable UUID spaceId, @Valid @RequestBody QuestionRequest request,
                              Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        var result = service.answer(userId, spaceId, request.question(), request.limitOrDefault());
        return new AnswerResponse(result.answer(), result.sources().stream().map(Source::from).toList());
    }

    public record QuestionRequest(
            @NotBlank @Size(max = 2000) String question,
            Integer limit) {
        int limitOrDefault() { return limit == null ? 5 : Math.max(1, Math.min(limit, 10)); }
    }

    public record AnswerResponse(String answer, List<Source> sources) {}

    public record Source(UUID chunkId, UUID documentId, String filename, int chunkIndex, String content, double similarity) {
        static Source from(VectorStoreService.RetrievedChunk c) {
            return new Source(c.id(), c.documentId(), c.filename(), c.chunkIndex(), c.content(), c.similarity());
        }
    }
}
