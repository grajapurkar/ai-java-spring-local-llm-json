package com.grp.aiapp.local.llm.service;

import com.grp.aiapp.local.llm.model.VectorDocument;
import com.grp.aiapp.local.llm.vector.InMemoryVectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RagService {

    private final EmbeddingService embeddingService;

    private final InMemoryVectorStore vectorStore;

    public String retrieve(String question) {

        float[] queryEmbedding =
                embeddingService
                        .generateEmbedding(question);

        List<VectorDocument> matches =
                vectorStore.similaritySearch(
                        queryEmbedding,
                        3
                );

        return matches.stream()
                .map(VectorDocument::getContent)
                .reduce("", (a, b) -> a + "\n" + b);
    }
}