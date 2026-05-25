package com.grp.aiapp.local.llm.model;
import lombok.Data;

import java.util.List;

@Data
public class PolicyDocument {

    private String policyId;

    private String category;

    private String plan;

    private String region;

    private String customerType;

    private String coverageLimit;

    private String duration;

    private List<String> addons;

    private String claimChannel;

    private String content;

    private float[] embedding;
}