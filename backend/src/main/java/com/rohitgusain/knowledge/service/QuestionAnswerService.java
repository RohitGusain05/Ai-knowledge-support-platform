package com.rohitgusain.knowledge.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionAnswerService {
    private final KnowledgeSpaceService knowledgeSpaceService;
    private final RagService ragService;
    private final AnswerGenerationClient answerGenerationClient;

    public QuestionAnswerService(KnowledgeSpaceService knowledgeSpaceService, RagService ragService,
                                 AnswerGenerationClient answerGenerationClient) {
        this.knowledgeSpaceService = knowledgeSpaceService;
        this.ragService = ragService;
        this.answerGenerationClient = answerGenerationClient;
    }

    public AnswerResult answer(UUID userId, UUID spaceId, String question, int limit) {
        knowledgeSpaceService.findOwnedSpace(userId, spaceId);
        List<VectorStoreService.RetrievedChunk> sources = ragService.retrieve(spaceId, question, limit);
        if (sources.isEmpty()) {
            return new AnswerResult("I couldn't find relevant information in this knowledge space.", List.of());
        }
        String answer = answerGenerationClient.generate(question, sources);
        return new AnswerResult(answer, sources);
    }

    public record AnswerResult(String answer, List<VectorStoreService.RetrievedChunk> sources) {}
}
