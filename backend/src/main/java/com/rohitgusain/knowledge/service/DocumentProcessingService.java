package com.rohitgusain.knowledge.service;

import com.rohitgusain.knowledge.entity.Document;
import com.rohitgusain.knowledge.entity.DocumentChunk;
import com.rohitgusain.knowledge.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DocumentProcessingService {
    private final TextExtractionService textExtractionService;
    private final DocumentChunker chunker;
    private final DocumentChunkRepository chunkRepository;

    public DocumentProcessingService(TextExtractionService textExtractionService,
                                     DocumentChunker chunker,
                                     DocumentChunkRepository chunkRepository) {
        this.textExtractionService = textExtractionService;
        this.chunker = chunker;
        this.chunkRepository = chunkRepository;
    }

    @Transactional
    public int process(Document document, MultipartFile sourceFile) {
        String text = textExtractionService.extract(sourceFile);
        List<String> chunks = chunker.chunk(text);
        for (int i = 0; i < chunks.size(); i++) {
            chunkRepository.save(new DocumentChunk(document, i, chunks.get(i)));
        }
        return chunks.size();
    }
}
