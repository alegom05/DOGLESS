package com.example.springdogless.controllers;

import com.example.springdogless.dao.UsuarioDao;
import com.example.springdogless.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"service", "/service"})
public class WebServiceController {

    @Autowired
    UsuarioDao usuarioDao;

    @GetMapping({"/list", "", "/"})
    public String listarProductos(Model model) {
        return "usuario/list";
    }

}
