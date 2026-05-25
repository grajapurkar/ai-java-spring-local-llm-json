package com.grp.aiapp.local.llm;

import com.grp.aiapp.local.llm.config.DataLoaderConfig;
import com.grp.aiapp.local.llm.model.VectorDocument;
import com.grp.aiapp.local.llm.service.EmbeddingService;
import com.grp.aiapp.local.llm.vector.InMemoryVectorStore;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataLoaderConfigUnitTest {

    @Test
    void loadPolicies_noLoadWhenDisabled() throws Exception {
        InMemoryVectorStore store = new InMemoryVectorStore();

        EmbeddingService embeddingService = new EmbeddingService() {
            @Override
            public float[] generateEmbedding(String text) {
                return new float[]{0.1f, 0.2f};
            }
        };

        DataLoaderConfig cfg = new DataLoaderConfig(store, embeddingService);

        // set private field loaderEnabled = false
        Field fEnabled = DataLoaderConfig.class.getDeclaredField("loaderEnabled");
        fEnabled.setAccessible(true);
        fEnabled.set(cfg, false);

        cfg.loadPolicies();

        // inspect private documents list inside InMemoryVectorStore
        Field docsField = InMemoryVectorStore.class.getDeclaredField("documents");
        docsField.setAccessible(true);
        List<VectorDocument> docs = (List<VectorDocument>) docsField.get(store);

        assertEquals(0, docs.size());
    }

    @Test
    void loadPolicies_limitsLinesWhenMaxLinesSet() throws Exception {
        InMemoryVectorStore store = new InMemoryVectorStore();

        EmbeddingService embeddingService = new EmbeddingService() {
            @Override
            public float[] generateEmbedding(String text) {
                return new float[]{0.1f, 0.2f};
            }
        };

        DataLoaderConfig cfg = new DataLoaderConfig(store, embeddingService);

        // set loaderEnabled = true and maxLines = 3
        Field fEnabled = DataLoaderConfig.class.getDeclaredField("loaderEnabled");
        fEnabled.setAccessible(true);
        fEnabled.set(cfg, true);

        Field fMax = DataLoaderConfig.class.getDeclaredField("maxLines");
        fMax.setAccessible(true);
        fMax.set(cfg, 3);

        cfg.loadPolicies();

        Field docsField = InMemoryVectorStore.class.getDeclaredField("documents");
        docsField.setAccessible(true);
        List<VectorDocument> docs = (List<VectorDocument>) docsField.get(store);

        assertEquals(3, docs.size());
    }
}

