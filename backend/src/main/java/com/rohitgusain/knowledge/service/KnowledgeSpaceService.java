package com.rohitgusain.knowledge.service;

import com.rohitgusain.knowledge.entity.KnowledgeSpace;
import com.rohitgusain.knowledge.entity.User;
import com.rohitgusain.knowledge.repository.KnowledgeSpaceRepository;
import com.rohitgusain.knowledge.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class KnowledgeSpaceService {
    private final KnowledgeSpaceRepository knowledgeSpaceRepository;
    private final UserRepository userRepository;

    public KnowledgeSpaceService(KnowledgeSpaceRepository knowledgeSpaceRepository, UserRepository userRepository) {
        this.knowledgeSpaceRepository = knowledgeSpaceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public KnowledgeSpace create(UUID ownerId, String name, String description) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return knowledgeSpaceRepository.save(new KnowledgeSpace(name.trim(), description, owner));
    }

    @Transactional(readOnly = true)
    public List<KnowledgeSpace> findOwnedBy(UUID ownerId) {
        return knowledgeSpaceRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId);
    }

    @Transactional(readOnly = true)
    public KnowledgeSpace findOwnedSpace(UUID ownerId, UUID spaceId) {
        return knowledgeSpaceRepository.findById(spaceId)
                .filter(space -> space.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new IllegalArgumentException("Knowledge space not found"));
    }
}
