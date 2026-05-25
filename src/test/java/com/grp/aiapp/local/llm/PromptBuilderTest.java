package com.grp.aiapp.local.llm;

import com.grp.aiapp.local.llm.service.PromptBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PromptBuilderTest {

    @Test
    void build_includesContextAndQuestion() {
        String ctx = "policy content";
        String q = "Does insurance cover flood?";

        String prompt = PromptBuilder.build(ctx, q);

        assertTrue(prompt.contains("Context:"));
        assertTrue(prompt.contains(ctx));
        assertTrue(prompt.contains("Question:"));
        assertTrue(prompt.contains(q));
    }
}

