package com.grp.aiapp.local.llm.service;

import com.grp.aiapp.local.llm.model.PolicyDocument;
import com.grp.aiapp.local.llm.model.VectorDocument;
import com.grp.aiapp.local.llm.vector.InMemoryVectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;



import java.util.List;

@Service
@RequiredArgsConstructor
public class RagService {

    private final EmbeddingService embeddingService;

    private final InMemoryVectorStore vectorStore;

    public List<PolicyDocument> retrieve(
            String query
    ) {

        float[] queryEmbedding =
                embeddingService
                        .generateEmbedding(query);

        return vectorStore.similaritySearch(
                queryEmbedding,
                5
        );
    }
}