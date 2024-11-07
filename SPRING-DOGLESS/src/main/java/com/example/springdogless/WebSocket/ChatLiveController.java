package com.example.springdogless.WebSocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatLiveController {

    @MessageMapping("/sendMessage") // El cliente envía aquí sus mensajes
    @SendTo("/topic/messages") // Todos los clientes escucharán en /topic/messages
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }
}
