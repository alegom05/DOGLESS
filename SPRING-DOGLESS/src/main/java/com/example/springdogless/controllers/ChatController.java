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

    @GetMapping("/inicio")
    public ResponseEntity<String> obtenerMensajeInicial() {
        String mensajeInicial = chatbotService.getMensajeInicial();
        return ResponseEntity.ok(mensajeInicial);
    }

    @PostMapping("/message")
    public ResponseEntity<String> processMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = chatbotService.procesarMensaje(message);
        return ResponseEntity.ok(response);
    }
}
