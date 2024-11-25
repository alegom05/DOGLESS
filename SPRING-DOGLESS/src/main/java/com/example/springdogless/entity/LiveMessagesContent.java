package com.example.springdogless.entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LiveMessagesContent {
    private String remitente; // "USUARIO" o "AGENTE"
    private String mensaje;
    private String roomId; // Sala única (ej: usuarioId-agenteId)
    private LocalDateTime timestamp;
    /*
    // Contenido del mensaje
    private String content;

    // Nombre del usuario que envía el mensaje
    private int senderId;

    // Sala de chat a la que pertenece el mensaje
    private String room;*/
}
