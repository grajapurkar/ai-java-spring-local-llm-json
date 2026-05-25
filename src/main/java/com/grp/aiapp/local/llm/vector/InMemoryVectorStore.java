package com.grp.aiapp.local.llm.vector;

import com.grp.aiapp.local.llm.model.PolicyDocument;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class InMemoryVectorStore {

    private final List<PolicyDocument> documents =
            new ArrayList<>();

    public void add(PolicyDocument document) {
        documents.add(document);
    }

    public List<PolicyDocument> similaritySearch(
            float[] queryEmbedding,
            int topK
    ) {

        return documents.stream()
                .sorted(
                        Comparator.comparingDouble(
                                doc ->
                                        -cosineSimilarity(
                                                queryEmbedding,
                                                doc.getEmbedding()
                                        )
                        )
                )
                .limit(topK)
                .toList();
    }

    private double cosineSimilarity(
            float[] a,
            float[] b
    ) {

        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {

            dot += a[i] * b[i];

            normA += Math.pow(a[i], 2);

            normB += Math.pow(b[i], 2);
        }

        return dot /
                (
                        Math.sqrt(normA)
                                * Math.sqrt(normB)
                );
    }
}