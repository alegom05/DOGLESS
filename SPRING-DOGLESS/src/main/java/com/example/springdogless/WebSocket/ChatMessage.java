package com.example.springdogless.WebSocket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String content;
    private String timestamp;

    // Getters y setters
}
