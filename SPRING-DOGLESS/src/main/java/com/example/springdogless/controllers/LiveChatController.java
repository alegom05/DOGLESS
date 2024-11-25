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
        Integer userId= Integer.parseInt(partsRoom[0]);

        String[] partsRemitente= message.getRemitente().split("-");
        Integer idEmisor=Integer.parseInt(partsRemitente[1]);

        /*USUARIO-35 o AGENTE-11*/
        Optional<Usuario> optionalUsuario=usuarioRepository.findById(idEmisor);

        if (optionalUsuario.isPresent()) {
            Usuario sender = optionalUsuario.get();
            LiveMessages messagedb  = new LiveMessages();

            String roomId2 = "room_" + userId;
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

    /*
    @MessageMapping("/chat/{room}")
    @SendTo("/topic/room/{room}")

    public LiveMessagesContent sendMessage(@DestinationVariable String room, LiveMessagesContent message, SimpMessageHeaderAccessor headerAccessor) {
        liveChatRoomService.markRoomAsActive(room);
        LiveMessages messagedb  = new LiveMessages();
        Usuario sender = usuarioRepository.getReferenceById(message.getSenderId());
        messagedb.setIdusuarios(sender);
        messagedb.setContenido(message.getContent());
        messagedb.setSala(message.getRoom());
        messagedb.setFechaenvio(LocalDateTime.now());
        liveMessagesRepository.save(messagedb);
        // Devolver el mensaje
        return message;
    }*/
}