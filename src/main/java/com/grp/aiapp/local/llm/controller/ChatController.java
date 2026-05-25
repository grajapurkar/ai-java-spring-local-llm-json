package com.grp.aiapp.local.llm.controller;
import com.grp.aiapp.local.llm.model.ChatRequest;
import com.grp.aiapp.local.llm.model.ChatResponse;
import com.grp.aiapp.local.llm.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatResponse ask(
            @RequestBody ChatRequest request) {

        String response =
                chatService.ask(request.question());

        return new ChatResponse(response);
    }
}