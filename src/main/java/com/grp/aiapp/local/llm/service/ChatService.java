package com.grp.aiapp.local.llm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.grp.aiapp.local.llm.model.PolicyDocument;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RagService ragService;

    private final LocalLlmService llmService;

    public String ask(String question) {

        List<PolicyDocument> docs = ragService.retrieve(question);

        String context = docs.stream()
                .map(PolicyDocument::getContent)
                .collect(Collectors.joining("\n"));

        String prompt = PromptBuilder.build(context, question);

        return llmService.generate(prompt);
    }
}