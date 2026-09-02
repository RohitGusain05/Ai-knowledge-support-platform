# AI Knowledge & Support Platform

An AI-powered knowledge and support platform that enables users to upload documents, process them asynchronously, search knowledge semantically, and receive context-aware answers with source citations.

## Architecture

React + TypeScript → Spring Boot → PostgreSQL / pgvector → Python FastAPI AI Service → Embeddings → Vector Retrieval → LLM

Document ingestion is asynchronous: uploads create persistent processing jobs, a scheduled worker claims jobs with database row locking, extracts text, chunks it, generates embeddings, stores vectors in pgvector, and retries failures up to three attempts.

## Technology Stack

### Frontend
- React
- TypeScript

### Backend
- Java 21
- Spring Boot 3.5
- Spring Data JPA / Hibernate
- REST APIs
- JWT + BCrypt authentication
- Apache PDFBox
- Scheduled background workers

### Data & Infrastructure
- PostgreSQL
- pgvector with HNSW cosine index
- Redis (infrastructure ready for caching/queue features)
- Docker / Docker Compose
- GitHub Actions

### AI Service
- Python 3.12
- FastAPI
- Sentence Transformers
- `all-MiniLM-L6-v2` embeddings (384 dimensions)

## Implemented

- User registration and JWT login
- Private knowledge spaces
- PDF, TXT and Markdown uploads
- Secure document storage with filename/path validation
- Persistent document processing jobs
- Pessimistic locking for worker job claiming
- Bounded automatic retries
- PDF/text extraction
- Overlapping text chunking
- Batch embedding generation through the FastAPI service
- PostgreSQL pgvector persistence
- HNSW vector index foundation
- Dockerized PostgreSQL, Redis, backend and AI service
- GitHub Actions Java 21 CI

## Planned

1. Semantic similarity retrieval
2. RAG question answering
3. Source citations in answers
4. Conversation history
5. Redis caching
6. React dashboard and document UI
7. LLM provider integration
8. Integration tests with Testcontainers
9. AWS deployment and observability

## Run locally

```bash
docker compose up --build
```

Backend: `http://localhost:8080`

AI service: `http://localhost:8000`

Health check: `http://localhost:8080/actuator/health`

## Engineering Goals

This project is designed to demonstrate production-oriented engineering beyond CRUD: secure API design, relational data modeling, asynchronous processing, database locking, vector search infrastructure, AI service integration, testing, containerization, CI/CD, and cloud-ready architecture.

## Status

🚧 In development — ingestion and embedding pipeline implemented; semantic retrieval and RAG answering are next.

## License

MIT
