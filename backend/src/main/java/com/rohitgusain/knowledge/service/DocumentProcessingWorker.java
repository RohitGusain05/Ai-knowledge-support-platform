package com.rohitgusain.knowledge.service;

import com.rohitgusain.knowledge.entity.DocumentProcessingJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DocumentProcessingWorker {
    private final DocumentProcessingJobService jobService;
    private final DocumentProcessingService processingService;

    public DocumentProcessingWorker(DocumentProcessingJobService jobService,
                                    DocumentProcessingService processingService) {
        this.jobService = jobService;
        this.processingService = processingService;
    }

    @Scheduled(fixedDelayString = "${processing.worker-delay-ms:1000}")
    public void poll() {
        DocumentProcessingJob job = jobService.claimNext();
        if (job == null) return;

        try {
            processingService.process(job.getDocument());
            jobService.complete(job.getId());
        } catch (Exception ex) {
            jobService.fail(job.getId(), ex.getMessage());
        }
    }
}
