package com.rohitgusain.knowledge.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_processing_jobs")
public class DocumentProcessingJob {
    @Id private UUID id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private Document document;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private ProcessingJobStatus status;
    @Column(nullable = false) private int attempts;
    @Column(name = "last_error") private String lastError;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @Column(name = "started_at") private OffsetDateTime startedAt;
    @Column(name = "completed_at") private OffsetDateTime completedAt;
    protected DocumentProcessingJob() {}

    public DocumentProcessingJob(Document document) {
        this.id = UUID.randomUUID(); this.document = document; this.status = ProcessingJobStatus.PENDING;
        this.createdAt = OffsetDateTime.now();
    }
    public void start() { status = ProcessingJobStatus.PROCESSING; attempts++; startedAt = OffsetDateTime.now(); lastError = null; }
    public void complete() { status = ProcessingJobStatus.COMPLETED; completedAt = OffsetDateTime.now(); }
    public void failOrRetry(String error) {
        lastError = error == null ? "Unknown processing error" : error.substring(0, Math.min(error.length(), 2000));
        if (attempts < 3) status = ProcessingJobStatus.PENDING;
        else { status = ProcessingJobStatus.FAILED; completedAt = OffsetDateTime.now(); }
    }
    public UUID getId() { return id; }
    public Document getDocument() { return document; }
    public ProcessingJobStatus getStatus() { return status; }
    public int getAttempts() { return attempts; }
}
