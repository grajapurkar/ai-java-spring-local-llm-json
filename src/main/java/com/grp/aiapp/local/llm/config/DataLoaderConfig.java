package com.grp.aiapp.local.llm.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grp.aiapp.local.llm.model.PolicyDocument;
import com.grp.aiapp.local.llm.service.EmbeddingService;
import com.grp.aiapp.local.llm.vector.InMemoryVectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.grp.aiapp.local.llm.config.LoaderProperties;


import java.io.InputStream;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(LoaderProperties.class)
public class DataLoaderConfig {
    private final InMemoryVectorStore vectorStore;

    private final EmbeddingService embeddingService;

    private final LoaderProperties loaderProperties;


    @PostConstruct
    public void loadPolicies() {

        if (!loaderProperties.isEnabled()) {
            System.out.println("Policy loader disabled via ai.loader.enabled=false");
            return;
        }

        try {

            ObjectMapper mapper =
                    new ObjectMapper();

            InputStream stream =
                    getClass()
                            .getResourceAsStream(
                                    "/policies.json"
                            );

            List<PolicyDocument> policies = mapper.readValue(stream, new TypeReference<>() {});

            // Respect maxLines property when > 0 (0 = load all)
            List<PolicyDocument> toLoad = policies;
            if (loaderProperties.getMaxLines() > 0 && policies.size() > loaderProperties.getMaxLines()) {
                toLoad = policies.subList(0, loaderProperties.getMaxLines());
            }

            for (PolicyDocument policy : toLoad) {

                String embeddingText =
                        buildEmbeddingText(policy);

                float[] embedding =
                        embeddingService
                                .generateEmbedding(
                                        embeddingText
                                );

                policy.setEmbedding(embedding);

                vectorStore.add(policy);
            }

            System.out.println("Loaded policies = " + toLoad.size());

        } catch (Exception ex) {

            throw new RuntimeException(ex);
        }
    }

    private String buildEmbeddingText(
            PolicyDocument policy
    ) {

        return """
                Category: %s
                Plan: %s
                Region: %s
                Customer Type: %s
                Coverage Limit: %s
                Duration: %s
                Addons: %s
                Claim Channel: %s
                Content: %s
                """
                .formatted(
                        policy.getCategory(),
                        policy.getPlan(),
                        policy.getRegion(),
                        policy.getCustomerType(),
                        policy.getCoverageLimit(),
                        policy.getDuration(),
                        policy.getAddons(),
                        policy.getClaimChannel(),
                        policy.getContent()
                );
    }
}