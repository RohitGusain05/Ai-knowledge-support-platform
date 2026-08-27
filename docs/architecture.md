# Architecture

## System Overview

The platform is organized into three application modules:

- **frontend** — React + TypeScript user interface
- **backend** — Java + Spring Boot API and core business logic
- **ai-service** — Python + FastAPI AI/RAG service

Supporting infrastructure:

- PostgreSQL for relational data
- pgvector for vector storage and similarity search
- Redis for caching and short-lived application state
- Docker for reproducible local development
- GitHub Actions for CI/CD

## Request Flow

```
Browser
  |
  v
React + TypeScript
  |
  | REST / JSON
  v
Spring Boot API
  |
  +---- PostgreSQL
  |
  +---- Redis
  |
  +---- AI Service
             |
             +---- Embeddings
             |
             +---- Vector Search
             |
             +---- LLM
```

## Architectural Decision

The initial implementation uses a **modular monolith** for the core application rather than immediately splitting everything into microservices.

The AI workload is separated into its own Python service because the AI ecosystem and model tooling are primarily Python-oriented. This provides a clear service boundary without unnecessary distributed-system complexity.

## Core Responsibilities

### Frontend
- Authentication UI
- Knowledge-space management
- Document management
- Search
- Chat experience
- Source citation display

### Backend
- Authentication and authorization
- User and knowledge-space management
- Document metadata
- API validation
- Business rules
- Persistence
- AI-service orchestration

### AI Service
- Document text processing
- Chunking
- Embedding generation
- Retrieval
- Prompt/context construction
- LLM interaction
- Grounded response generation

## Security Principles

- Passwords are never stored in plaintext.
- Protected APIs require authentication.
- Authorization is enforced server-side.
- Users can only access resources they are permitted to access.
- Secrets are provided through environment variables.
- Uploaded documents are treated as untrusted input.

## Development Principle

Build the simplest correct version first. Add Redis, asynchronous jobs, advanced retrieval, observability, and cloud infrastructure when the corresponding feature is implemented and tested.
