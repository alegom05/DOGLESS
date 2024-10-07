package com.example.springdogless.controllers;

import com.example.springdogless.Repository.UsuarioRepository;
import com.example.springdogless.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;


import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    UsuarioRepository usuarioRepository;

    // Mapea la vista del login
    @GetMapping("/loginForm")
    public String login() {
        return "loginForm"; // Esto renderiza la vista login.html
    }

    // Mapea la vista de registro
    @GetMapping("/register")
    public String register() {
        return "register"; // Esto renderiza la vista register.html
    }

    // Mapea la vista de recuperación de contraseña
    @GetMapping("/olvidastecontasenha")
    public String olvidastecontasenha() {
        return "olvidastecontasenha"; // Esto renderiza la vista olvidastecontasenha.html
    }

    // Mapea la vista de crear nueva contraseña (en caso se quiera cambiar después de la recuperación)
    @GetMapping("/crearnuevacontrasenha")
    public String crearnuevacontrasenha() {
        return "crearnuevacontrasenha"; // Esto renderiza la vista crearnuevacontrasenha.html
    }



    @GetMapping("/informacion-de-contacto")
    public String informaciondecontacto() {
        return "informacion-de-contacto"; // Esto renderiza la vista informacion-de-contacto.html
    }


    @GetMapping("/politica-de-privacidad")
    public String politicadeprivacidad() {
        return "politica-de-privacidad"; // Esto renderiza la vista politica-de-privacidad.html
    }

    @GetMapping("/imagen/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Integer id) {
        System.out.println("Llamando a getImage con ID: " + id); // Agrega este log
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            byte[] imagenBytes = usuario.getFotoperfil();

            if (imagenBytes != null) {
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imagenBytes);
            } else {
                // Carga la imagen por defecto desde el classpath
                try {
                    ClassPathResource imgFile = new ClassPathResource("static/assets/img/default.jpg");
                    byte[] defaultImage = Files.readAllBytes(imgFile.getFile().toPath());
                    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(defaultImage);
                } catch (IOException e) {
                    // Manejo de errores al cargar la imagen
                    return ResponseEntity.internalServerError().build();
                }
            }
        }

        System.out.println("No se encontró producto con ID: " + id); // Agrega este log
        return ResponseEntity.notFound().build();
    }

}

