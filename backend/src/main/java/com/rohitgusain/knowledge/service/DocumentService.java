package com.rohitgusain.knowledge.service;

import com.rohitgusain.knowledge.entity.Document;
import com.rohitgusain.knowledge.entity.KnowledgeSpace;
import com.rohitgusain.knowledge.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "text/plain",
            "text/markdown"
    );

    private final DocumentRepository documentRepository;
    private final KnowledgeSpaceService knowledgeSpaceService;
    private final Path storageRoot;

    public DocumentService(DocumentRepository documentRepository,
                           KnowledgeSpaceService knowledgeSpaceService) {
        this.documentRepository = documentRepository;
        this.knowledgeSpaceService = knowledgeSpaceService;
        this.storageRoot = Paths.get(
                System.getenv().getOrDefault("DOCUMENT_STORAGE_PATH", "./storage/documents")
        ).toAbsolutePath().normalize();
    }

    @Transactional
    public Document upload(UUID ownerId, UUID spaceId, MultipartFile file) {
        validate(file);

        KnowledgeSpace space = knowledgeSpaceService.findOwnedSpace(ownerId, spaceId);
        String storageKey = spaceId + "/" + UUID.randomUUID() + ".bin";

        try {
            Path destination = storageRoot.resolve(storageKey).normalize();
            if (!destination.startsWith(storageRoot)) {
                throw new IllegalArgumentException("Invalid storage path");
            }

            Files.createDirectories(destination.getParent());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            Document document = new Document(
                    space,
                    sanitizeFilename(file.getOriginalFilename()),
                    storageKey,
                    file.getContentType(),
                    file.getSize()
            );

            return documentRepository.save(document);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to store document");
        }
    }

    @Transactional(readOnly = true)
    public List<Document> list(UUID ownerId, UUID spaceId) {
        knowledgeSpaceService.findOwnedSpace(ownerId, spaceId);
        return documentRepository.findAllByKnowledgeSpaceIdOrderByUploadedAtDesc(spaceId);
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("A document file is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size must not exceed 10 MB");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF, TXT, and Markdown files are supported");
        }
    }

    private String sanitizeFilename(String filename) {
        String safe = filename == null ? "document" : Paths.get(filename).getFileName().toString();
        return safe.isBlank() ? "document" : safe;
    }
}
