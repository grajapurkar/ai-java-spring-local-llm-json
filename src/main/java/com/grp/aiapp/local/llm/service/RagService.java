package com.grp.aiapp.local.llm.service;

import com.grp.aiapp.local.llm.model.PolicyDocument;
import com.grp.aiapp.local.llm.vector.InMemoryVectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {

    private final EmbeddingService embeddingService;

    private final InMemoryVectorStore vectorStore;

    /**
     * Retrieve top-K matching documents.
     */
    public List<PolicyDocument> retrieve(String query) {

        float[] queryEmbedding =
                embeddingService
                        .generateEmbedding(query);

        return vectorStore.similaritySearch(
                queryEmbedding,
                5
        );
    }
}