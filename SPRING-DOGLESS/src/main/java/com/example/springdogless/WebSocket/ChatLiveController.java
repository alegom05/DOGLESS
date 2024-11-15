package com.example.springdogless.WebSocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class ChatLiveController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatLiveController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/sendMessage") // Maneja los mensajes enviados a /app/sendMessage
    public void sendMessage(ChatMessage message) {
        // Envía el mensaje al destinatario específico
        messagingTemplate.convertAndSend("/topic/messages/" + message.getReceiverId(), message);
    }
}
