package com.grp.aiapp.local.llm;

import com.grp.aiapp.local.llm.service.EmbeddingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class ChatControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public EmbeddingService embeddingService() {
            return new EmbeddingService() {
                @Override
                public float[] generateEmbedding(String text) {
                    // deterministic small vector for fast, reliable tests
                    int dim = 384;
                    float[] v = new float[dim];
                    Arrays.fill(v, 0.01f);
                    return v;
                }
            };
        }
    }

    @Test
    public void shouldReturnChatResponse() {
        Map<String, String> req = Map.of("question", "hello");

        ResponseEntity<String> resp = restTemplate.postForEntity("/api/chat", req, String.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody() != null && resp.getBody().contains("LOCAL AI RESPONSE"));
    }
}

