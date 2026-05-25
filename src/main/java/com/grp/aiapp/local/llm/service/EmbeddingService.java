package com.grp.aiapp.local.llm.service;

import org.springframework.stereotype.Service;

import java.util.Map;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.onnxruntime.*;
import jakarta.annotation.PostConstruct;

import java.nio.LongBuffer;

@Service
public class EmbeddingService {

    private OrtEnvironment environment;

    private OrtSession session;

    private HuggingFaceTokenizer tokenizer;
    @PostConstruct
    public void init() {

        try {

            environment =
                    OrtEnvironment.getEnvironment();

            var modelUrl =
                    getClass()
                            .getResource("/model/model.onnx");

            if (modelUrl == null) {
                throw new RuntimeException(
                        "model.onnx not found"
                );
            }

            String modelPath =
                    java.nio.file.Paths
                            .get(modelUrl.toURI())
                            .toString();
            System.out.println(
                    "MODEL PATH = " + modelPath
            );
            session =
                    environment.createSession(
                            modelPath,
                            new OrtSession.SessionOptions()
                    );

            var tokenizerUrl =
                    getClass()
                            .getResource(
                                    "/model/tokenizer.json"
                            );
            System.out.println(
                    "tokenizerUrl = " + tokenizerUrl
            );
            if (tokenizerUrl == null) {
                throw new RuntimeException(
                        "tokenizer.json not found"
                );
            }

            String tokenizerPath =
                    java.nio.file.Paths
                            .get(tokenizerUrl.toURI())
                            .toString();
            System.out.println(
                    "tokenizerPath = " + tokenizerPath
            );
            tokenizer =
                    HuggingFaceTokenizer.newInstance(
                            java.nio.file.Paths.get(
                                    tokenizerPath
                            )
                    );

            System.out.println(
                    "Tokenizer Loaded Successfully"
            );


            System.out.println(
                    "MiniLM tokenizer loaded"
            );

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public float[] generateEmbedding(String text) {

        try {

            // ----------------------------------------
            // TOKENIZE INPUT
            // ----------------------------------------

            var encoding = tokenizer.encode(text);

            long[] inputIds =
                    encoding.getIds();

            long[] attentionMask =
                    encoding.getAttentionMask();

            // ----------------------------------------
            // TOKEN TYPE IDS
            // REQUIRED FOR BERT/MiniLM MODELS
            // ----------------------------------------

            long[] tokenTypeIds =
                    new long[inputIds.length];

            for (int i = 0; i < tokenTypeIds.length; i++) {
                tokenTypeIds[i] = 0;
            }

            // ----------------------------------------
            // CREATE INPUT TENSORS
            // ----------------------------------------

            OnnxTensor inputTensor =
                    OnnxTensor.createTensor(
                            environment,
                            LongBuffer.wrap(inputIds),
                            new long[]{1, inputIds.length}
                    );

            OnnxTensor attentionTensor =
                    OnnxTensor.createTensor(
                            environment,
                            LongBuffer.wrap(attentionMask),
                            new long[]{1, attentionMask.length}
                    );

            OnnxTensor tokenTypeTensor =
                    OnnxTensor.createTensor(
                            environment,
                            LongBuffer.wrap(tokenTypeIds),
                            new long[]{1, tokenTypeIds.length}
                    );

            // ----------------------------------------
            // MODEL INPUT MAP
            // ----------------------------------------

            Map<String, OnnxTensor> inputs =
                    Map.of(
                            "input_ids",
                            inputTensor,

                            "attention_mask",
                            attentionTensor,

                            "token_type_ids",
                            tokenTypeTensor
                    );

            // ----------------------------------------
            // RUN MODEL
            // ----------------------------------------

            OrtSession.Result result =
                    session.run(inputs);

            // ----------------------------------------
            // EXTRACT EMBEDDINGS
            // ----------------------------------------

            float[][][] embeddings =
                    (float[][][]) result.get(0)
                            .getValue();

            // ----------------------------------------
            // MEAN POOLING
            // ----------------------------------------

            float[] pooledEmbedding =
                    meanPooling(embeddings[0]);

            System.out.println(
                    "Embedding dimension = "
                            + pooledEmbedding.length
            );

            return pooledEmbedding;

        } catch (Exception ex) {

            throw new RuntimeException(ex);
        }
    }


    private float[] meanPooling(float[][] tokenEmbeddings) {

        int dimension =
                tokenEmbeddings[0].length;

        float[] pooled =
                new float[dimension];

        for (float[] token : tokenEmbeddings) {

            for (int i = 0; i < dimension; i++) {
                pooled[i] += token[i];
            }
        }

        for (int i = 0; i < dimension; i++) {
            pooled[i] /= tokenEmbeddings.length;
        }

        return pooled;
    }


}
//@Service
//public class EmbeddingService {
//
//    private static final int DIMENSION = 384;
//
//    public float[] generateEmbedding(String text) {
//
//        Random random =
//                new Random(text.hashCode());
//
//        float[] vector =
//                new float[DIMENSION];
//
//        for (int i = 0; i < DIMENSION; i++) {
//            vector[i] = random.nextFloat();
//        }
//
//        return vector;
//    }
//}