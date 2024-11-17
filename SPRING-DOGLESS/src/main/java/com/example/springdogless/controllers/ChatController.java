package com.example.springdogless.controllers;

import com.example.springdogless.services.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/message")
    public ResponseEntity<String> processMessage(@RequestBody String message) {
        String respuesta = chatbotService.buscarRespuestaAdaptada(message);

        if (respuesta == null) {
            respuesta = "Lo siento, no entiendo tu pregunta. ¿Puedes reformularla?";
        }

        return ResponseEntity.ok(respuesta);
    }
}
