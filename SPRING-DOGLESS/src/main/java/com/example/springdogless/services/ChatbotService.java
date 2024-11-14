package com.example.springdogless.services;

import com.example.springdogless.config.HuggingFaceConfig;
import com.example.springdogless.entity.ChatMessage;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class ChatbotService {
    private final RestTemplate restTemplate;
    private final HuggingFaceConfig huggingFaceConfig;
    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final ComplaintBookService complaintBookService;

    // URL base de la API de Hugging Face
    private static final String HF_API_URL = "https://api-inference.huggingface.co/models/";

    public ChatbotService(RestTemplate restTemplate,
                          HuggingFaceConfig huggingFaceConfig,
                          ObjectMapper objectMapper,
                          OrderService orderService,
                          ComplaintBookService complaintBookService) {
        this.restTemplate = restTemplate;
        this.huggingFaceConfig = huggingFaceConfig;
        this.objectMapper = objectMapper;
        this.orderService = orderService;
        this.complaintBookService = complaintBookService;
    }

    public ChatMessage processMessage(String userId, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(userId);
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(LocalDateTime.now());

        String response = callHuggingFaceAPI(message);
        chatMessage.setResponse(response);

        if (isOrderRequest(message)) {
            return handleOrderGeneration(chatMessage);
        } else if (isComplaintRequest(message)) {
            return handleComplaintBook(chatMessage);
        }

        return chatMessage;
    }

    private String callHuggingFaceAPI(String message) {
        String url = HF_API_URL + huggingFaceConfig.getModel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(huggingFaceConfig.getApiKey());

        // Formato del prompt para Mistral
        String prompt = String.format("""
        <s>[INST] Eres un asistente virtual de comercio electrónico. 
        Ayudas con consultas generales, órdenes de compra y libro de reclamaciones.
        
        Consulta del usuario: %s [/INST]</s>
        """, message);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("inputs", prompt);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_length", 500);
        requestBody.put("top_p", 0.95);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Cambiar la deserialización para leer un array en lugar de un solo objeto
            JsonNode responseBody = objectMapper.readTree(response.getBody());
            if (responseBody.isArray() && responseBody.size() > 0) {
                // Acceder al primer elemento del array
                return responseBody.get(0).path("generated_text").asText()
                        .replace(prompt, "")  // Remover el prompt de la respuesta
                        .trim();
            } else {
                return "Lo siento, no se pudo procesar la respuesta del chatbot.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Lo siento, ha ocurrido un error al procesar tu mensaje. Por favor, intenta nuevamente.";
        }
    }


    private boolean isOrderRequest(String message) {
        String lowercaseMessage = message.toLowerCase();
        return lowercaseMessage.contains("comprar") ||
                lowercaseMessage.contains("orden") ||
                lowercaseMessage.contains("pedido");
    }

    private boolean isComplaintRequest(String message) {
        String lowercaseMessage = message.toLowerCase();
        return lowercaseMessage.contains("reclamo") ||
                lowercaseMessage.contains("queja") ||
                lowercaseMessage.contains("libro de reclamaciones");
    }

    private ChatMessage handleOrderGeneration(ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.ORDER_GENERATION);
        // Implementar lógica específica para generar órdenes
        return chatMessage;
    }

    private ChatMessage handleComplaintBook(ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.COMPLAINT_BOOK);
        // Implementar lógica específica para el libro de reclamaciones
        return chatMessage;
    }
}