# AI Knowledge & Support Platform

An AI-powered knowledge and support platform that lets users upload private documents, process them asynchronously, retrieve relevant knowledge with semantic search, and receive grounded answers with source citations.

## Architecture

React + TypeScript → Spring Boot → PostgreSQL / pgvector → Python FastAPI AI Service → Embeddings → Vector Retrieval → Local LLM

### RAG flow

1. User uploads a PDF, TXT, or Markdown document.
2. A persistent processing job is created.
3. A background worker extracts and chunks the document.
4. FastAPI generates normalized 384-dimensional embeddings.
5. Embeddings are stored in PostgreSQL `pgvector` with an HNSW cosine index.
6. A question is embedded using the same model.
7. pgvector returns the most similar chunks from the user's knowledge space.
8. The retrieved context is sent to the LLM with an instruction to answer only from that context.
9. The API returns the generated answer plus the retrieved source chunks and similarity scores.

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
- Redis
- Docker / Docker Compose
- GitHub Actions

### AI Service
- Python 3.12
- FastAPI
- Sentence Transformers
- `all-MiniLM-L6-v2` embeddings (384 dimensions)
- Ollama-compatible local LLM generation

## Implemented

- User registration and JWT login
- Private knowledge spaces and ownership checks
- PDF, TXT and Markdown uploads
- Secure document storage and path validation
- Persistent document processing jobs
- Pessimistic locking for concurrent workers
- Automatic retry with bounded attempts
- PDF/text extraction and overlapping chunking
- Batch embedding generation
- PostgreSQL pgvector persistence and similarity retrieval
- Grounded RAG question answering
- Retrieved source metadata and similarity scores
- Dockerized PostgreSQL, Redis, backend and AI service
- GitHub Actions Java 21 CI

## API examples

After authentication, semantic retrieval is available at:

`POST /api/v1/knowledge-spaces/{spaceId}/search`

Question answering is available at:

`POST /api/v1/knowledge-spaces/{spaceId}/questions`

Example request:

```json
{
  "question": "What is the refund policy?",
  "limit": 5
}
```

## Run locally

```bash
docker compose up --build
```

For local LLM generation, install Ollama on the host and pull the configured model:

```bash
ollama pull llama3.2:3b
```

You can override the model with environment variables:

```bash
LLM_BASE_URL=http://host.docker.internal:11434 LLM_MODEL=llama3.2:3b docker compose up --build
```

Backend: `http://localhost:8080`

AI service: `http://localhost:8000`

Health check: `http://localhost:8080/actuator/health`

## Engineering Goals

This project demonstrates production-oriented engineering beyond CRUD: secure API design, relational data modeling, asynchronous processing, database locking, vector search, RAG orchestration, AI service integration, testing, containerization, CI/CD, and cloud-ready architecture.

## Status

🚧 In development — document ingestion, embeddings, semantic retrieval, and grounded RAG answering are implemented. Frontend experience, conversation history, caching, integration tests, and cloud deployment remain next.

## License

MIT
