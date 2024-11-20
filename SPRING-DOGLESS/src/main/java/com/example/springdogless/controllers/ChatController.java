package com.example.springdogless.controllers;

import com.example.springdogless.services.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/message")
    public ResponseEntity<String> processMessage(@RequestBody Map<String, String> request) {
        // Obtén el mensaje enviado en el cuerpo del JSON
        String message = request.get("message");

        // Pasa el mensaje al servicio para obtener la respuesta
        String response = chatbotService.procesarMensaje(message);

        return ResponseEntity.ok(response);
    }
}
