package com.example.springdogless.controllers;

import com.example.springdogless.entity.ChatMessage;
import com.example.springdogless.services.ChatbotService;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;


@Controller
public class ChatController {
    private final ChatbotService chatbotService;

    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    // Endpoint para mostrar la página que contiene el chat
    @GetMapping("/chat")
    public String chatPage() {
        return "chat"; // Esto buscará chat.html en templates
    }

    // Endpoint REST para procesar mensajes
    @PostMapping("/api/chat/message")
    @ResponseBody
    public ResponseEntity<ChatMessage> processMessage(@RequestBody ChatMessage message) {
        ChatMessage response = chatbotService.processMessage(
                message.getUserId(),
                message.getMessage()
        );
        return ResponseEntity.ok(response);
    }
}