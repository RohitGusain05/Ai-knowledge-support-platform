CREATE TABLE document_chunks (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    chunk_index INTEGER NOT NULL,
    content TEXT NOT NULL,
    token_count INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_document_chunk_index UNIQUE (document_id, chunk_index),
    CONSTRAINT chk_document_chunk_index CHECK (chunk_index >= 0),
    CONSTRAINT chk_document_chunk_token_count CHECK (token_count IS NULL OR token_count > 0)
);

CREATE INDEX idx_document_chunks_document_id ON document_chunks (document_id);
