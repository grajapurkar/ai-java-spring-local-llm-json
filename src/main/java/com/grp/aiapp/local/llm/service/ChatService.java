package com.grp.aiapp.local.llm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RagService ragService;

    private final LocalLlmService llmService;

    public String ask(String question) {

        String context =
                ragService.retrieve(question);

        String prompt =
                PromptBuilder.build(
                        context,
                        question
                );

        return llmService.generate(prompt);
    }
}