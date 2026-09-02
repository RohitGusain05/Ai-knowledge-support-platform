package com.rohitgusain.knowledge.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentChunker {
    private static final int CHUNK_SIZE = 1200;
    private static final int OVERLAP = 200;

    public List<String> chunk(String text) {
        if (text == null || text.isBlank()) return List.of();
        String normalized = text.replaceAll("\\s+", " ").trim();
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < normalized.length()) {
            int end = Math.min(start + CHUNK_SIZE, normalized.length());
            if (end < normalized.length()) {
                int boundary = normalized.lastIndexOf(' ', end);
                if (boundary > start + CHUNK_SIZE / 2) end = boundary;
            }
            chunks.add(normalized.substring(start, end));
            if (end == normalized.length()) break;
            start = Math.max(0, end - OVERLAP);
        }
        return chunks;
    }
}
