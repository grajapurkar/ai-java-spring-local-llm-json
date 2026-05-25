package main.test.llm;

import com.grp.aiapp.local.llm.model.VectorDocument;
import com.grp.aiapp.local.llm.vector.InMemoryVectorStore;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryVectorStoreTest {

    @Test
    void similaritySearch_returnsTopKAndCorrectOrder() {
        InMemoryVectorStore store = new InMemoryVectorStore();

        float[] e1 = new float[]{1.0f, 0.0f};
        float[] e2 = new float[]{0.0f, 1.0f};

        store.add(new VectorDocument("d1", "doc-one", e1));
        store.add(new VectorDocument("d2", "doc-two", e2));

        // Query identical to e1 should return doc-one first
        List<com.grp.aiapp.local.llm.model.VectorDocument> top1 = store.similaritySearch(new float[]{1.0f, 0.0f}, 1);

        assertEquals(1, top1.size());
        assertEquals("doc-one", top1.get(0).getContent());

        // Query orthogonal should return second when appropriate
        List<com.grp.aiapp.local.llm.model.VectorDocument> both = store.similaritySearch(new float[]{0.7071f, 0.7071f}, 2);
        assertEquals(2, both.size());
        // similarity to both is equal, but comparator uses stable order based on stream; ensure both docs present
        assertTrue(both.stream().anyMatch(d -> d.getContent().equals("doc-one")));
        assertTrue(both.stream().anyMatch(d -> d.getContent().equals("doc-two")));
    }
}

