package com.rohitgusain.knowledge.controller;

import com.rohitgusain.knowledge.entity.KnowledgeSpace;
import com.rohitgusain.knowledge.service.KnowledgeSpaceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/knowledge-spaces")
public class KnowledgeSpaceController {
    private final KnowledgeSpaceService knowledgeSpaceService;

    public KnowledgeSpaceController(KnowledgeSpaceService knowledgeSpaceService) {
        this.knowledgeSpaceService = knowledgeSpaceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(Authentication authentication, @Valid @RequestBody CreateKnowledgeSpaceRequest request) {
        UUID userId = (UUID) authentication.getPrincipal();
        KnowledgeSpace space = knowledgeSpaceService.create(userId, request.name(), request.description());
        return Map.of("id", space.getId(), "name", space.getName(), "description", space.getDescription());
    }

    @GetMapping
    public List<Map<String, Object>> list(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return knowledgeSpaceService.findOwnedBy(userId).stream()
                .map(space -> Map.<String, Object>of("id", space.getId(), "name", space.getName(), "description", space.getDescription()))
                .toList();
    }

    @GetMapping("/{spaceId}")
    public Map<String, Object> get(Authentication authentication, @PathVariable UUID spaceId) {
        UUID userId = (UUID) authentication.getPrincipal();
        KnowledgeSpace space = knowledgeSpaceService.findOwnedSpace(userId, spaceId);
        return Map.of("id", space.getId(), "name", space.getName(), "description", space.getDescription());
    }

    public record CreateKnowledgeSpaceRequest(
            @NotBlank @Size(max = 150) String name,
            @Size(max = 500) String description) {}
}
