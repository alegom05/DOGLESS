package com.example.springdogless.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Mapea la vista del login
    @GetMapping("/login")
    public String login() {
        return "login"; // Esto renderiza la vista login.html
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
}

