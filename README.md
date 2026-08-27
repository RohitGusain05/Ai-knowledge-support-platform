# AI Knowledge & Support Platform

An AI-powered knowledge and support platform that enables users to upload documents, search knowledge semantically, and receive context-aware answers with source citations.

## Vision

Build a production-style full-stack application that combines modern software engineering with retrieval-augmented generation (RAG).

Users will be able to:
- Create private knowledge spaces
- Upload and manage documents
- Search knowledge semantically
- Ask questions about their documents
- Receive grounded AI answers
- Inspect the sources used to generate each answer

## Planned Architecture

React + TypeScript → Spring Boot → PostgreSQL / Redis → Python AI Service → Embeddings / Vector Search → LLM

The application will begin as a modular system and introduce asynchronous processing and additional infrastructure only when the feature requires it.

## Technology Stack

### Frontend
- React
- TypeScript

### Backend
- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- REST APIs

### Data & Infrastructure
- PostgreSQL
- pgvector
- Redis
- Docker
- GitHub Actions
- AWS

### AI Service
- Python
- FastAPI
- Embeddings
- Retrieval-Augmented Generation (RAG)
- LLM API

### Testing
- JUnit
- Mockito
- Integration testing

## Planned Features

1. Authentication and authorization
2. Knowledge spaces and access control
3. Document upload and processing
4. Text extraction and chunking
5. Embedding generation and vector storage
6. Semantic retrieval
7. AI question answering
8. Source citations
9. Conversation history
10. Redis caching
11. Background processing
12. Automated testing
13. Dockerized development
14. CI/CD
15. AWS deployment

## Engineering Goals

This project is intentionally designed to demonstrate more than CRUD development:

- Clean architecture
- Secure API design
- Database design and indexing
- Asynchronous processing
- Caching
- AI integration
- Testing
- Containerization
- CI/CD
- Cloud deployment
- Production-oriented error handling and observability

## Status

🚧 In development

## License

MIT
