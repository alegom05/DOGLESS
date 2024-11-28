package com.example.springdogless.controllers;

import com.example.springdogless.Repository.DistritoRepository;
import com.example.springdogless.Repository.RolRepository;
import com.example.springdogless.Repository.UsuarioRepository;
import com.example.springdogless.dao.UsuarioDao;
import com.example.springdogless.entity.Distrito;
import com.example.springdogless.entity.Rol;
import com.example.springdogless.entity.Usuario;
import com.example.springdogless.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j  // Agregar esta anotación para logging

@Controller
@RequestMapping({"", "/"})
public class LoginController {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    EmailService emailService;
    @Autowired
    UsuarioDao usuarioDao;
    @Autowired
    DistritoRepository distritoRepository;
    @Autowired
    RolRepository rolRepository;


    // Mapea la vista del login
    @GetMapping({"", "/loginForm"})
    public String login() {
        return "login/loginForm"; // Esto renderiza la vista login.html
    }

    // Ruta para agentes
    @GetMapping("/loginAgente")
    public String loginAgente() {
        return "login/loginFormAgente";
    }
    // Mapea la vista de registro
    @GetMapping("/api/getUserByDni")
    @ResponseBody
    public Map<String, String> getUserByDni(@RequestParam String dni) {
        String token = "YOUR_API_TOKEN";
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://dniruc.apisperu.com/api/v1/dni/" + dni + "?token=" + token;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> userData = response.getBody();

        // Assuming the JSON response has "nombres", "apellidoPaterno", and "apellidoMaterno"
        Map<String, String> result = new HashMap<>();
        result.put("nombre", (String) userData.get("nombres"));
        result.put("apellido", (String) userData.get("apellidoPaterno") + " " + userData.get("apellidoMaterno"));
        System.out.println("******"+userData.get("nombres"));
        return result;
    }

    @GetMapping("/register")
    public String registro(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("usuario", new Usuario()); // Añade un nuevo objeto Usuario al modelo
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@ModelAttribute Usuario usuario, @RequestParam String confirmarContrasena, Model model) {
        if (!usuario.getPwd().equals(confirmarContrasena)) {
            model.addAttribute("error", "Las contraseñas no coinciden. Por favor, verifica.");
            return "register"; // Devuelve la misma vista del formulario
        }
        Rol rol = rolRepository.findById(4)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Asigna el rol predeterminado al usuario
        usuario.setRol(rol);
        // Assuming you have a UsuarioService to handle saving the user
        if (usuario.getDistrito() == null || usuario.getDistrito().getIddistritos() == null) {
            throw new IllegalArgumentException("El distrito no puede ser nulo.");
        }

        Distrito distrito = distritoRepository.findById(usuario.getDistrito().getIddistritos())
                .orElseThrow(() -> new RuntimeException("Distrito not found"));

        usuario.setDistrito(distrito);
        usuario.setZona(distrito.getZona());
        usuario.setBorrado(1);  // Usuario no borrado
        usuario.setEstado("activo");// Estado activo
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        usuario.setPwd(passwordEncoder.encode(usuario.getPwd()));
        usuarioRepository.save(usuario);
        // Envía el correo de confirmación
        emailService.enviarCorreo(
                usuario.getEmail(),
                "Confirmación de creación de cuenta",
                "Estimado/a " + usuario.getNombre() +
                        ",\n\nTu cuenta ha sido creada exitosamente. Ahora puedes iniciar sesión en nuestro sistema.\n\nGracias por registrarte,\nEquipo de Dogless"
        );
        model.addAttribute("message", "Registration successful!");
        return "redirect:/loginForm"; // Redirect to login or another page after registration
    }

    // Add this method to handle GET requests
    @GetMapping("/olvidastecontasenha")
    public String showForgotPasswordForm() {
        return "login/olvidastecontasenha";
    }

    // Mapea la vista de recuperación de contraseña

    @PostMapping("/olvidastecontasenha")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            // Genera un token único (podrías almacenarlo en la base de datos)
            String resetToken = UUID.randomUUID().toString();

            // Crea el enlace de restablecimiento
            String resetLink = "http://yourdomain.com/crearnuevacontrasenha?token=" + resetToken;

            // Crear y enviar correo con alias
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(email);
            helper.setSubject("Restablecimiento de contraseña");
            helper.setText("Para restablecer tu contraseña, haz clic en el siguiente enlace:\n\n" + resetLink, true);

            // Aquí defines el alias y el remitente
            helper.setFrom("admin@dogless.com", "Dogless Admin");

            emailSender.send(mimeMessage);

            model.addAttribute("mensaje", "Se ha enviado un correo con instrucciones para restablecer tu contraseña.");
            return "login/olvidastecontasenha";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al enviar el correo. Por favor, inténtalo de nuevo.");
            return "login/olvidastecontasenha";
        }
    }

    // Mapea la vista de crear nueva contraseña (en caso se quiera cambiar después de la recuperación)
    @GetMapping("/crearnuevacontrasenha")
    public String crearnuevacontrasenha() {
        return "crearnuevacontrasenha"; // Esto renderiza la vista crearnuevacontrasenha.html
    }

    @GetMapping("/cambiarcontrasenha")
    public String cambiarcontrasenha() {
        return "login/cambiarcontrasenha"; // Esto renderiza la vista crearnuevacontrasenha.html
    }


    @GetMapping("/informacion-de-contacto")
    public String informaciondecontacto() {
        return "informacion-de-contacto"; // Esto renderiza la vista informacion-de-contacto.html
    }


    @GetMapping("/politica-de-privacidad")
    public String politicadeprivacidad() {
        return "politica-de-privacidad"; // Esto renderiza la vista politica-de-privacidad.html
    }


    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        // Lógica para generar el token de restablecimiento de contraseña

        // Envío del correo
        emailService.sendEmail(email, "Restablecimiento de contraseña", "Haga clic en el enlace para restablecer su contraseña.");

        return ResponseEntity.ok("Correo enviado.");
    }
}

