package com.rohitgusain.knowledge.repository;

import com.rohitgusain.knowledge.entity.DocumentProcessingJob;
import com.rohitgusain.knowledge.entity.ProcessingJobStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.UUID;

public interface DocumentProcessingJobRepository extends JpaRepository<DocumentProcessingJob, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select j from DocumentProcessingJob j where j.status = :status order by j.createdAt asc")
    Optional<DocumentProcessingJob> findNextPending(ProcessingJobStatus status);
}
