package com.grp.aiapp.local.llm.model;


public class VectorDocument {

    private String id;

    private String content;

    private float[] embedding;

    public VectorDocument() {
    }

    public VectorDocument(
            String id,
            String content,
            float[] embedding
    ) {
        this.id = id;
        this.content = content;
        this.embedding = embedding;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public float[] getEmbedding() {
        return embedding;
    }
}