package com.grp.aiapp.local.llm.service;

import org.springframework.stereotype.Service;

@Service
public class LocalLlmService {

    public String generate(String prompt) {

        return """
                ===== LOCAL AI RESPONSE =====

                %s

                =============================
                """.formatted(prompt);
    }
}