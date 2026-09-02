package com.rohitgusain.knowledge.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_chunks")
public class DocumentChunk {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "chunk_index", nullable = false)
    private int chunkIndex;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected DocumentChunk() {}

    public DocumentChunk(Document document, int chunkIndex, String content) {
        this.id = UUID.randomUUID();
        this.document = document;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public Document getDocument() { return document; }
    public int getChunkIndex() { return chunkIndex; }
    public String getContent() { return content; }
}
