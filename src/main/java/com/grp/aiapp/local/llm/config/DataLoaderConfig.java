package com.grp.aiapp.local.llm.config;

import com.grp.aiapp.local.llm.model.VectorDocument;
import com.grp.aiapp.local.llm.service.EmbeddingService;
import com.grp.aiapp.local.llm.vector.InMemoryVectorStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
public class DataLoaderConfig {

    private final InMemoryVectorStore vectorStore;

    private final EmbeddingService embeddingService;

    // Enable/disable the startup loader (useful for fast local iteration)
    @Value("${ai.loader.enabled:true}")
    private boolean loaderEnabled;

    // Max number of lines to load from policies.txt. 0 = load all (default).
    @Value("${ai.loader.maxLines:0}")
    private int maxLines;

    @PostConstruct
    public void loadPolicies() {

        if (!loaderEnabled) {
            System.out.println("DataLoader disabled via ai.loader.enabled=false");
            return;
        }

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     getClass().getResourceAsStream("/policies.txt")
                             )
                     )) {

            Stream<String> lines = reader.lines().filter(line -> !line.isBlank());

            if (maxLines > 0) {
                lines = lines.limit(maxLines);
                System.out.println("DataLoader: limiting policies loaded to " + maxLines + " lines");
            }

            lines.forEach(line -> {

                float[] embedding =
                        embeddingService
                                .generateEmbedding(line);

                VectorDocument document =
                        new VectorDocument(
                                UUID.randomUUID().toString(),
                                line,
                                embedding
                        );

                vectorStore.add(document);
            });

            System.out.println("Policies loaded into vector store");

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}