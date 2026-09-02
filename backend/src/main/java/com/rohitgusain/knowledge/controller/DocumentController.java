package com.rohitgusain.knowledge.controller;

import com.rohitgusain.knowledge.entity.Document;
import com.rohitgusain.knowledge.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/knowledge-spaces/{spaceId}/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> upload(
            Authentication authentication,
            @PathVariable UUID spaceId,
            @RequestParam("file") MultipartFile file) {

        UUID userId = (UUID) authentication.getPrincipal();
        Document document = documentService.upload(userId, spaceId, file);

        return toResponse(document);
    }

    @GetMapping
    public List<Map<String, Object>> list(
            Authentication authentication,
            @PathVariable UUID spaceId) {

        UUID userId = (UUID) authentication.getPrincipal();
        return documentService.list(userId, spaceId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Map<String, Object> toResponse(Document document) {
        return Map.of(
                "id", document.getId(),
                "filename", document.getOriginalFilename(),
                "contentType", document.getContentType(),
                "fileSize", document.getFileSize(),
                "processingStatus", document.getProcessingStatus().name(),
                "uploadedAt", document.getUploadedAt()
        );
    }
}
