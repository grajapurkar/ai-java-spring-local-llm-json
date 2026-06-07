package main.test.llm;

import com.grp.aiapp.local.llm.model.PolicyDocument;
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

        PolicyDocument p1 = new PolicyDocument();
        p1.setPolicyId("d1");
        p1.setContent("doc-one");
        p1.setEmbedding(e1);
        store.add(p1);

        PolicyDocument p2 = new PolicyDocument();
        p2.setPolicyId("d2");
        p2.setContent("doc-two");
        p2.setEmbedding(e2);
        store.add(p2);

        // Query identical to e1 should return doc-one first
        List<PolicyDocument> top1 = store.similaritySearch(new float[]{1.0f, 0.0f}, 1);

        assertEquals(1, top1.size());
        assertEquals("doc-one", top1.get(0).getContent());

        // Query orthogonal should return second when appropriate
        List<PolicyDocument> both = store.similaritySearch(new float[]{0.7071f, 0.7071f}, 2);
        assertEquals(2, both.size());
        // similarity to both is equal, but comparator uses stable order based on stream; ensure both docs present
        assertTrue(both.stream().anyMatch(d -> d.getContent().equals("doc-one")));
        assertTrue(both.stream().anyMatch(d -> d.getContent().equals("doc-two")));
    }
}

