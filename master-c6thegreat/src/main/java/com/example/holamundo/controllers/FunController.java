package com.example.holamundo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/fun")
public class FunController {

    @ResponseBody
    @GetMapping(value = { "/",""})
    public String funprincipal() {
        return "Welcome! Everything is fun!";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("msg", "¡Este es otro mensaje!");
        model.addAttribute("nombre", "Capibara");
        model.addAttribute("cantidad", 20);
        return "home";
    }




}
