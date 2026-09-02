package com.rohitgusain.knowledge.repository;

import com.rohitgusain.knowledge.entity.KnowledgeSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface KnowledgeSpaceRepository extends JpaRepository<KnowledgeSpace, UUID> {
    List<KnowledgeSpace> findAllByOwnerIdOrderByCreatedAtDesc(UUID ownerId);
}
