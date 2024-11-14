package com.example.springdogless.controllers;/*package com.example.springdogless.controllers;


import com.example.springdogless.Repository.MessageRepository;
import com.example.springdogless.entity.Message;
import com.example.springdogless.services.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private MessageRepository messageRepository;

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody Message userMessage) {
        // Guardamos el mensaje del usuario
        userMessage.setSender("USER");
        messageRepository.save(userMessage);

        // Generamos y guardamos la respuesta del bot
        Message botResponse = new Message();
        botResponse.setSender("BOT");
        botResponse.setContent(chatbotService.generateResponse(userMessage.getContent()));
        messageRepository.save(botResponse);

        return ResponseEntity.ok(botResponse);
    }
}
 */