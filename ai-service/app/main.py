import os

import requests
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from sentence_transformers import SentenceTransformer

app = FastAPI(title="AI Knowledge Support Service", version="0.2.0")
model = SentenceTransformer("sentence-transformers/all-MiniLM-L6-v2")
LLM_BASE_URL = os.getenv("LLM_BASE_URL", "http://host.docker.internal:11434")
LLM_MODEL = os.getenv("LLM_MODEL", "llama3.2:3b")


class EmbeddingRequest(BaseModel):
    texts: list[str] = Field(min_length=1, max_length=64)


class EmbeddingResponse(BaseModel):
    embeddings: list[list[float]]
    dimension: int


class GenerateRequest(BaseModel):
    question: str = Field(min_length=1, max_length=4000)
    context: str = Field(min_length=1, max_length=30000)


class GenerateResponse(BaseModel):
    answer: str


@app.get("/health")
def health():
    return {"status": "UP", "service": "ai-service"}


@app.post("/api/v1/embeddings", response_model=EmbeddingResponse)
def embeddings(request: EmbeddingRequest):
    vectors = model.encode(request.texts, normalize_embeddings=True).tolist()
    return {"embeddings": vectors, "dimension": len(vectors[0])}


@app.post("/api/v1/generate", response_model=GenerateResponse)
def generate(request: GenerateRequest):
    prompt = (
        "Answer the user's question using ONLY the supplied context. "
        "If the context does not contain the answer, say that the knowledge base does not contain enough information. "
        "Do not invent facts. Be concise.\n\n"
        f"Context:\n{request.context}\n\nQuestion:\n{request.question}"
    )
    try:
        response = requests.post(
            f"{LLM_BASE_URL.rstrip('/')}/api/generate",
            json={"model": LLM_MODEL, "prompt": prompt, "stream": False},
            timeout=120,
        )
        response.raise_for_status()
        data = response.json()
        return {"answer": data.get("response", "").strip()}
    except requests.RequestException as exc:
        raise HTTPException(status_code=502, detail=f"LLM service unavailable: {exc}")
