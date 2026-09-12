package com.rohitgusain.knowledge.controller;

import com.rohitgusain.knowledge.service.KnowledgeSpaceService;
import com.rohitgusain.knowledge.service.RagService;
import com.rohitgusain.knowledge.service.VectorStoreService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/knowledge-spaces/{spaceId}/search")
public class RagController {
    private final RagService ragService;
    private final KnowledgeSpaceService knowledgeSpaceService;

    public RagController(RagService ragService, KnowledgeSpaceService knowledgeSpaceService) {
        this.ragService = ragService;
        this.knowledgeSpaceService = knowledgeSpaceService;
    }

    @PostMapping
    public SearchResponse search(@PathVariable UUID spaceId, @RequestBody SearchRequest request, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        knowledgeSpaceService.findOwnedSpace(userId, spaceId);
        List<VectorStoreService.RetrievedChunk> chunks = ragService.retrieve(spaceId, request.question(), request.limit());
        return new SearchResponse(request.question(), chunks.stream().map(Source::from).toList());
    }

    public record SearchRequest(String question, Integer limit) {
        public int limit() { return limit == null ? 5 : limit; }
    }
    public record SearchResponse(String question, List<Source> sources) {}
    public record Source(UUID chunkId, UUID documentId, int chunkIndex, String content, double similarity) {
        static Source from(VectorStoreService.RetrievedChunk c) {
            return new Source(c.id(), c.documentId(), c.chunkIndex(), c.content(), c.similarity());
        }
    }
}
