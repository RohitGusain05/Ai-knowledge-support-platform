package com.rohitgusain.knowledge.service;

import org.springframework.stereotype.Service;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DocumentStorageService {
    private final Path root = Paths.get(System.getenv().getOrDefault("DOCUMENT_STORAGE_PATH", "./storage/documents"))
            .toAbsolutePath().normalize();

    public Path resolve(String storageKey) {
        Path path = root.resolve(storageKey).normalize();
        if (!path.startsWith(root)) throw new IllegalArgumentException("Invalid storage path");
        return path;
    }
}
