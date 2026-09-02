package com.rohitgusain.knowledge.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TextExtractionService {
    public String extract(MultipartFile file) {
        try {
            if ("application/pdf".equals(file.getContentType())) {
                try (var document = Loader.loadPDF(file.getBytes())) {
                    return new PDFTextStripper().getText(document);
                }
            }
            return new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to extract document text");
        }
    }

    public String extract(Path file, String contentType) {
        try {
            byte[] bytes = Files.readAllBytes(file);
            if ("application/pdf".equals(contentType)) {
                try (var document = Loader.loadPDF(bytes)) {
                    return new PDFTextStripper().getText(document);
                }
            }
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to extract document text");
        }
    }
}
