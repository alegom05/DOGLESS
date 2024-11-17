package com.example.springdogless.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatbotService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private List<JsonNode> preguntasRespuestas;

    // Inyectar propiedades desde application.properties
    @Value("${huggingface.apiKey}")
    private String apiKey;

    @Value("${huggingface.model}")
    private String model;

    public ChatbotService(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        cargarPreguntasDesdeJson();
    }

    // Método para cargar preguntas desde el archivo JSON
    private void cargarPreguntasDesdeJson() {
        try (InputStream inputStream = getClass().getResourceAsStream("/preguntas_respuestas.json")) {
            JsonNode root = objectMapper.readTree(inputStream);
            preguntasRespuestas = new ArrayList<>();
            root.get("preguntas_respuestas").forEach(preguntasRespuestas::add);
        } catch (IOException e) {
            e.printStackTrace();
            preguntasRespuestas = new ArrayList<>(); // Lista vacía en caso de error
        }
    }

    // Método para buscar respuesta adaptada (JSON o IA)
    public String buscarRespuestaAdaptada(String mensaje) {
        // Primero busca una respuesta predefinida en el JSON
        String respuesta = buscarRespuesta(mensaje);

        // Si no encuentra una respuesta, llama a la IA
        if (respuesta == null) {
            respuesta = callHuggingFaceAPI(mensaje);
        }

        return respuesta;
    }

    // Método para buscar una respuesta en el JSON
    public String buscarRespuesta(String mensaje) {
        String mensajeNormalizado = mensaje.toLowerCase();

        // Busca coincidencias con las claves del JSON
        for (JsonNode entry : preguntasRespuestas) {
            String clave = entry.get("clave").asText();
            if (mensajeNormalizado.contains(clave)) {
                return entry.get("respuesta").asText();
            }
        }
        return null; // Si no hay coincidencias, devuelve null
    }

    // Método para llamar al modelo de Hugging Face
    private String callHuggingFaceAPI(String mensaje) {
        String url = String.format("https://api-inference.huggingface.co/models/%s", model);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); // Usar la API Key configurada en application.properties

        String prompt = String.format("Pregunta del usuario: %s", mensaje);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("inputs", prompt);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            JsonNode responseBody = objectMapper.readTree(response.getBody());
            if (responseBody.isArray() && responseBody.size() > 0) {
                return responseBody.get(0).path("generated_text").asText().trim();
            } else {
                return "Lo siento, no puedo procesar tu mensaje en este momento.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Ocurrió un error al procesar tu solicitud.";
        }
    }
}
