package com.grp.aiapp.local.llm.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grp.aiapp.local.llm.model.PolicyDocument;
import com.grp.aiapp.local.llm.service.EmbeddingService;
import com.grp.aiapp.local.llm.vector.InMemoryVectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoaderConfig {
    private final InMemoryVectorStore vectorStore;

    private final EmbeddingService embeddingService;


    @PostConstruct
    public void loadPolicies() {

        try {

            ObjectMapper mapper =
                    new ObjectMapper();

            InputStream stream =
                    getClass()
                            .getResourceAsStream(
                                    "/policies.json"
                            );

            List<PolicyDocument> policies =
                    mapper.readValue(
                            stream,
                            new TypeReference<>() {}
                    );

            for (PolicyDocument policy : policies) {

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

            System.out.println(
                    "Loaded policies = "
                            + policies.size()
            );

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