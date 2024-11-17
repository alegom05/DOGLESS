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
import java.time.LocalTime;
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

    @Value("${chatbot.horario.inicio}")
    private String horarioInicio;

    @Value("${chatbot.horario.fin}")
    private String horarioFin;

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

    // Método para manejar la respuesta adaptada
    public String procesarMensaje(String mensaje) {
        if (dentroHorarioLaboral()) {
            // Responder con la IA o las respuestas predefinidas
            return buscarRespuestaAdaptada(mensaje);
        } else {
            return "El agente de compras no está disponible en este momento. Continuemos con el autoservicio.";
        }
    }

    // Verificar si está dentro del horario laboral
    private boolean dentroHorarioLaboral() {
        LocalTime inicio = LocalTime.parse(horarioInicio);
        LocalTime fin = LocalTime.parse(horarioFin);
        LocalTime ahora = LocalTime.now();

        return ahora.isAfter(inicio) && ahora.isBefore(fin);
    }

    // Método para buscar respuesta adaptada (JSON o IA)
    public String buscarRespuestaAdaptada(String mensaje) {
        String respuesta = buscarRespuesta(mensaje);

        if (respuesta == null) {
            if (mensaje.toLowerCase().contains("orden de compra")) {
                return generarOrdenDeCompra();
            } else if (mensaje.toLowerCase().contains("libro de reclamaciones")) {
                return manejarReclamaciones();
            } else {
                respuesta = callHuggingFaceAPI(mensaje);
            }
        }

        return respuesta;
    }

    // Método para buscar una respuesta en el JSON
    public String buscarRespuesta(String mensaje) {
        String mensajeNormalizado = mensaje.toLowerCase();

        for (JsonNode entry : preguntasRespuestas) {
            String clave = entry.get("clave").asText();
            if (mensajeNormalizado.contains(clave)) {
                return entry.get("respuesta").asText();
            }
        }
        return null;
    }

    // Método para manejar reclamaciones
    private String manejarReclamaciones() {
        return "Puedes acceder a nuestro libro de reclamaciones virtual en este enlace: [Libro de Reclamaciones Virtual].";
    }

    // Método para generar una orden de compra
    private String generarOrdenDeCompra() {
        // Lógica simulada para generar una orden
        String numeroOrden = "ORD-" + System.currentTimeMillis();
        return String.format("Tu orden de compra ha sido generada exitosamente. Número de orden: %s.", numeroOrden);
    }

    // Método para llamar al modelo de Hugging Face
    private String callHuggingFaceAPI(String mensaje) {
        String url = String.format("https://api-inference.huggingface.co/models/%s", model);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

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
