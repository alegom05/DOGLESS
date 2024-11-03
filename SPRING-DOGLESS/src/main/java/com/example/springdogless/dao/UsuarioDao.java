package com.example.springdogless.dao;

import com.example.springdogless.entity.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class UsuarioDao {

    public List<Usuario> listarUsuario() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Usuario[]> response = restTemplate.getForEntity(
                "http://localhost:8080/usuario", Usuario[].class
        );

        return Arrays.asList(response.getBody());
    }


}
