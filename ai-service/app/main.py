from fastapi import FastAPI
from pydantic import BaseModel, Field
from sentence_transformers import SentenceTransformer

app = FastAPI(title="AI Knowledge Support Service", version="0.1.0")
model = SentenceTransformer("sentence-transformers/all-MiniLM-L6-v2")


class EmbeddingRequest(BaseModel):
    texts: list[str] = Field(min_length=1, max_length=64)


class EmbeddingResponse(BaseModel):
    embeddings: list[list[float]]
    dimension: int


@app.get("/health")
def health():
    return {"status": "UP", "service": "ai-service"}


@app.post("/api/v1/embeddings", response_model=EmbeddingResponse)
def embeddings(request: EmbeddingRequest):
    vectors = model.encode(request.texts, normalize_embeddings=True).tolist()
    return {"embeddings": vectors, "dimension": len(vectors[0])}
