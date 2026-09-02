# AI Service

Python service reserved for document processing, embeddings, retrieval, and LLM orchestration.

The first implementation keeps text extraction in the backend so the processing contract can be tested independently. AI-specific workloads will move behind this service boundary as the RAG pipeline matures.
