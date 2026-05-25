package com.grp.aiapp.local.llm.service;

import org.springframework.stereotype.Service;

@Service
public class PolicyToolService {

    public String checkCoverage(String input) {

        if (input.toLowerCase().contains("accident")) {
            return "Coverage available for accident claims";
        }

        return "Coverage validation pending";
    }
}