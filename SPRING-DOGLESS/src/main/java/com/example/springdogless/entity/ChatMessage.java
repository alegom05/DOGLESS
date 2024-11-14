package com.example.springdogless.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String userId;
    private String message;
    private LocalDateTime timestamp;
    private MessageType type;
    private String response;

    public enum MessageType {

        ORDER_GENERATION,
        COMPLAINT_BOOK,
        QUESTION,
        COMPLAINT,
        ORDER_INQUIRY,
        GENERAL
    }

}
