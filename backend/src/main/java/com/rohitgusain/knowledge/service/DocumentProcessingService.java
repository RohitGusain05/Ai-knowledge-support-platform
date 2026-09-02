package com.rohitgusain.knowledge.service;

import com.rohitgusain.knowledge.entity.Document;
import com.rohitgusain.knowledge.entity.DocumentChunk;
import com.rohitgusain.knowledge.repository.DocumentChunkRepository;
import com.rohitgusain.knowledge.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DocumentProcessingService {
    private final TextExtractionService textExtractionService;
    private final DocumentChunker chunker;
    private final DocumentChunkRepository chunkRepository;
    private final DocumentRepository documentRepository;
    private final AiEmbeddingClient embeddingClient;
    private final VectorStoreService vectorStoreService;
    private final Path storageRoot;

    public DocumentProcessingService(TextExtractionService textExtractionService,
                                     DocumentChunker chunker,
                                     DocumentChunkRepository chunkRepository,
                                     DocumentRepository documentRepository,
                                     AiEmbeddingClient embeddingClient,
                                     VectorStoreService vectorStoreService) {
        this.textExtractionService = textExtractionService;
        this.chunker = chunker;
        this.chunkRepository = chunkRepository;
        this.documentRepository = documentRepository;
        this.embeddingClient = embeddingClient;
        this.vectorStoreService = vectorStoreService;
        this.storageRoot = Paths.get(System.getenv().getOrDefault("DOCUMENT_STORAGE_PATH", "./storage/documents"))
                .toAbsolutePath().normalize();
    }

    @Transactional
    public int process(Document document) {
        document.markProcessing();
        documentRepository.save(document);

        Path source = storageRoot.resolve(document.getStorageKey()).normalize();
        if (!source.startsWith(storageRoot)) throw new IllegalStateException("Invalid document storage path");

        String text = textExtractionService.extract(source, document.getContentType());
        List<String> chunks = chunker.chunk(text);
        if (chunks.isEmpty()) throw new IllegalArgumentException("Document contains no extractable text");

        chunkRepository.deleteAllByDocumentId(document.getId());
        List<DocumentChunk> savedChunks = new java.util.ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) savedChunks.add(chunkRepository.save(new DocumentChunk(document, i, chunks.get(i))));

        for (int start = 0; start < savedChunks.size(); start += 32) {
            int end = Math.min(start + 32, savedChunks.size());
            List<DocumentChunk> batch = savedChunks.subList(start, end);
            List<List<Double>> embeddings = embeddingClient.embed(batch.stream().map(DocumentChunk::getContent).toList());
            vectorStoreService.storeEmbeddings(batch.stream().map(DocumentChunk::getId).toList(), embeddings);
        }

        document.markCompleted();
        documentRepository.save(document);
        return chunks.size();
    }
}
