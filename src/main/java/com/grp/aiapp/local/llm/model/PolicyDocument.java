package com.grp.aiapp.local.llm.model;

public class PolicyDocument {

    private String id;
    private String content;

    public PolicyDocument() {
    }

    public PolicyDocument(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}