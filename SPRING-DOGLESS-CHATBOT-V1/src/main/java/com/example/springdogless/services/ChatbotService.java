package com.example.springdogless.services;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ChatbotService {
    private final Map<Pattern, String> responses;

    public ChatbotService() {
        responses = new HashMap<>();
        // Agregamos algunos patrones de respuesta básicos
        responses.put(Pattern.compile("(?i).*hola.*"), "¡Hola! ¿En qué puedo ayudarte?");
        responses.put(Pattern.compile("(?i).*ayuda.*"), "Puedo ayudarte con consultas sobre nuestros productos y servicios.");
        responses.put(Pattern.compile("(?i).*gracias.*"), "¡De nada! ¿Hay algo más en lo que pueda ayudarte?");
        responses.put(Pattern.compile("(?i).*adios.*"), "¡Hasta luego! Que tengas un buen día.");
    }

    public String generateResponse(String userMessage) {
        for (Map.Entry<Pattern, String> entry : responses.entrySet()) {
            if (entry.getKey().matcher(userMessage).matches()) {
                return entry.getValue();
            }
        }
        return "Lo siento, no entiendo. ¿Podrías reformular tu pregunta?";
    }
}