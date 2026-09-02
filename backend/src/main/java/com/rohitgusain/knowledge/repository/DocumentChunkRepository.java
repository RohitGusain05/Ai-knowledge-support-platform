package com.rohitgusain.knowledge.repository;

import com.rohitgusain.knowledge.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, UUID> {
    long countByDocumentId(UUID documentId);
}
