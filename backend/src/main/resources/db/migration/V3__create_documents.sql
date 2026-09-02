CREATE TABLE documents (
    id UUID PRIMARY KEY,
    knowledge_space_id UUID NOT NULL REFERENCES knowledge_spaces(id) ON DELETE CASCADE,
    original_filename VARCHAR(255) NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    processing_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMPTZ,
    CONSTRAINT chk_documents_file_size CHECK (file_size >= 0),
    CONSTRAINT chk_documents_processing_status CHECK (
        processing_status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')
    )
);

CREATE INDEX idx_documents_knowledge_space_id ON documents (knowledge_space_id);
CREATE INDEX idx_documents_processing_status ON documents (processing_status);
