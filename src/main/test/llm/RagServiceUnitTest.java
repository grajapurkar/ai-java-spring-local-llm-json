package main.test.llm;

import com.grp.aiapp.local.llm.model.VectorDocument;
import com.grp.aiapp.local.llm.service.EmbeddingService;
import com.grp.aiapp.local.llm.service.RagService;
import com.grp.aiapp.local.llm.vector.InMemoryVectorStore;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RagServiceUnitTest {

    @Test
    void retrieve_returnsConcatenatedContentsOfMatches() {
        // Mock embedding service that maps any text to [1,0]
        EmbeddingService embeddingService = new EmbeddingService() {
            @Override
            public float[] generateEmbedding(String text) {
                return new float[]{1.0f, 0.0f};
            }
        };

        InMemoryVectorStore store = new InMemoryVectorStore();

        float[] e1 = new float[]{1.0f, 0.0f};
        float[] e2 = new float[]{0.0f, 1.0f};

        store.add(new VectorDocument(UUID.randomUUID().toString(), "Policy A", e1));
        store.add(new VectorDocument(UUID.randomUUID().toString(), "Policy B", e2));

        RagService ragService = new RagService(embeddingService, store);

        String result = ragService.retrieve("anything");

        // Should include Policy A (most similar) and may include Policy B depending on topK and values
        assertTrue(result.contains("Policy A"));
    }
}

