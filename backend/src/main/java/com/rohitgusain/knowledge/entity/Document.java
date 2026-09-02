package com.rohitgusain.knowledge.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "knowledge_space_id", nullable = false)
    private KnowledgeSpace knowledgeSpace;
    @Column(name = "original_filename", nullable = false, length = 255) private String originalFilename;
    @Column(name = "storage_key", nullable = false, length = 500) private String storageKey;
    @Column(name = "content_type", nullable = false, length = 100) private String contentType;
    @Column(name = "file_size", nullable = false) private long fileSize;
    @Enumerated(EnumType.STRING) @Column(name = "processing_status", nullable = false, length = 30) private ProcessingStatus processingStatus;
    @Column(name = "uploaded_at", nullable = false) private OffsetDateTime uploadedAt;
    @Column(name = "processed_at") private OffsetDateTime processedAt;
    protected Document() {}
    public Document(KnowledgeSpace knowledgeSpace, String originalFilename, String storageKey, String contentType, long fileSize) {
        this.id = UUID.randomUUID(); this.knowledgeSpace = knowledgeSpace; this.originalFilename = originalFilename;
        this.storageKey = storageKey; this.contentType = contentType; this.fileSize = fileSize;
        this.processingStatus = ProcessingStatus.PENDING; this.uploadedAt = OffsetDateTime.now();
    }
    public void markProcessing() { this.processingStatus = ProcessingStatus.PROCESSING; }
    public void markCompleted() { this.processingStatus = ProcessingStatus.COMPLETED; this.processedAt = OffsetDateTime.now(); }
    public void markFailed() { this.processingStatus = ProcessingStatus.FAILED; }
    public UUID getId() { return id; } public KnowledgeSpace getKnowledgeSpace() { return knowledgeSpace; }
    public String getOriginalFilename() { return originalFilename; } public String getStorageKey() { return storageKey; }
    public String getContentType() { return contentType; } public long getFileSize() { return fileSize; }
    public ProcessingStatus getProcessingStatus() { return processingStatus; } public OffsetDateTime getUploadedAt() { return uploadedAt; }
    public OffsetDateTime getProcessedAt() { return processedAt; }
}
