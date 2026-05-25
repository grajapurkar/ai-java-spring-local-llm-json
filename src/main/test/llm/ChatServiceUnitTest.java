package main.test.llm;

import com.grp.aiapp.local.llm.service.ChatService;
import com.grp.aiapp.local.llm.service.LocalLlmService;
import com.grp.aiapp.local.llm.service.RagService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatServiceUnitTest {

    @Test
    void ask_buildsPromptAndCallsLlm() {
        RagService rag = new RagService(null, null) {
            @Override
            public String retrieve(String question) {
                return "context-for-" + question;
            }
        };

        LocalLlmService llm = new LocalLlmService() {
            @Override
            public String generate(String prompt) {
                return "LLM_OUTPUT:" + prompt;
            }
        };

        ChatService chat = new ChatService(rag, llm);

        String out = chat.ask("how are you?");

        // LocalLlmService.generate was overridden to include the prompt
        assertTrue(out.startsWith("LLM_OUTPUT:"));
    }
}

