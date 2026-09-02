package com.rohitgusain.knowledge.service;

import com.rohitgusain.knowledge.entity.Document;
import com.rohitgusain.knowledge.entity.DocumentProcessingJob;
import com.rohitgusain.knowledge.entity.ProcessingJobStatus;
import com.rohitgusain.knowledge.repository.DocumentProcessingJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DocumentProcessingJobService {
    private final DocumentProcessingJobRepository repository;

    public DocumentProcessingJobService(DocumentProcessingJobRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void enqueue(Document document) {
        repository.save(new DocumentProcessingJob(document));
    }

    @Transactional
    public DocumentProcessingJob claimNext() {
        return repository.findNextPending(ProcessingJobStatus.PENDING)
                .map(job -> { job.start(); return job; })
                .orElse(null);
    }

    @Transactional
    public void complete(UUID jobId) {
        repository.findById(jobId).ifPresent(job -> job.complete());
    }

    @Transactional
    public void fail(UUID jobId, String error) {
        repository.findById(jobId).ifPresent(job -> job.fail(error));
    }
}
