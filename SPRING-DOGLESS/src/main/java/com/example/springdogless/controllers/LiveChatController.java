package com.example.springdogless.controllers;

import com.example.springdogless.Repository.*;
import com.example.springdogless.entity.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.session.DelegatingIndexResolver;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Optional;


@Controller
public class LiveChatController {

    @Autowired
    private LiveMessagesRepository liveMessagesRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;


    @MessageMapping("/chat/{roomId}/sendMessage")
    @SendTo("/topic/chat/{roomId}")
    public LiveMessagesContent handleMessage(@DestinationVariable String roomId, LiveMessagesContent message, SimpMessageHeaderAccessor headerAccessor) {
        /*roomId=35-11*/
        String[] partsRoom = roomId.split("-");
        int userId3= Integer.parseInt(partsRoom[0]);

        /*USUARIO-35 o AGENTE-11*/
        Optional<Usuario> optionalUsuario=usuarioRepository.findById(Integer.valueOf(message.getUserId()));

        if (optionalUsuario.isPresent()) {
            Usuario sender = optionalUsuario.get();
            LiveMessages messagedb  = new LiveMessages();

            String roomId2 = "room_" + userId3;
            messagedb.setIdusuarios(sender);
            messagedb.setContenido(message.getMensaje());
            messagedb.setSala(roomId2);
            messagedb.setFechaenvio(LocalDateTime.now());
            liveMessagesRepository.save(messagedb);
        }


        message.setTimestamp(LocalDateTime.now());
        message.setRoomId(roomId); // Opcional
        return message;
    }
}