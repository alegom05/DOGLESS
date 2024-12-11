package com.example.springdogless.controllers;

import com.example.springdogless.Repository.ReclamoRepository;
import com.example.springdogless.Repository.UsuarioRepository;
import com.example.springdogless.entity.Reclamo;
import com.example.springdogless.entity.Usuario;
import com.example.springdogless.services.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ReclamoRepository reclamoRepository;
    private final ChatbotService chatbotService;

    @Autowired
    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @GetMapping("/inicio")
    public ResponseEntity<String> obtenerMensajeInicial() {
        String mensajeInicial = chatbotService.getMensajeInicial();
        return ResponseEntity.ok(mensajeInicial);
    }

    @PostMapping("/message")
    public ResponseEntity<String> processMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response;

        // Verifica si el mensaje debe ser procesado en el flujo de compra
        if (chatbotService.esFlujoDeCompra(message)) {
            response = chatbotService.procesarFlujoCompra(null, message); // Pasar null para userId
        } else {
            response = chatbotService.procesarMensaje(message);
        }

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(response);
    }



    @PostMapping("/reclamo")
    public ResponseEntity<String> crearReclamoDesdeChat(@RequestBody Map<String, String> request) {
        String idUsuario = request.get("idusuario");
        String descripcion = request.get("descripcion");

        if (idUsuario == null || descripcion == null || descripcion.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Se requiere un usuario y una descripción para el reclamo.");
        }

        // Verificar si el usuario existe
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(Integer.parseInt(idUsuario));
        if (optionalUsuario.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Usuario no encontrado.");
        }

        // Crear y guardar el reclamo
        Usuario usuario = optionalUsuario.get();
        Reclamo reclamo = new Reclamo();
        reclamo.setUsuario(usuario);
        reclamo.setDescripcion(descripcion);

        reclamoRepository.save(reclamo);

        return ResponseEntity.ok("Reclamo creado exitosamente.");
    }

    @PostMapping("/flujoCompra")
    public ResponseEntity<String> flujoCompra(@RequestBody Map<String, String> request) {
        String userId = request.get("userId"); // Obtén el userId del request body
        String mensaje = request.get("message");

        if (userId == null || mensaje == null) {
            return ResponseEntity.badRequest().body("Error: userId y message son obligatorios.");
        }

        // Procesa el flujo de compra dinámico para el usuario
        String respuesta = chatbotService.procesarFlujoCompra(userId, mensaje);

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(respuesta);
    }




}
