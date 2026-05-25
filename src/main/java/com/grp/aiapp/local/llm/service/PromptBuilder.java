package com.grp.aiapp.local.llm.service;

public class PromptBuilder {

    public static String build(String context, String question) {

        return """
                Use the following context.

                Context:
                %s

                Question:
                %s

                Generate helpful response.
                """.formatted(context, question);
    }
}