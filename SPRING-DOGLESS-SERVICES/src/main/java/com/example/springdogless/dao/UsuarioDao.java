package com.example.springdogless.dao;

import com.example.springdogless.entity.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class UsuarioDao {

    public Usuario mostrarNombreYApellido() {
        RestTemplate restTemplate = new RestTemplate();
        String dni = "10471272";
        String token ="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFnb21lem1AcHVjcC5lZHUucGUifQ.LJBKMWNN5CUGCYySOygI6dblO41SdD_6ocS__g02gMc";
        ResponseEntity<Usuario> response = restTemplate.getForEntity(
                "https://dniruc.apisperu.com/api/v1/dni/" + dni + "?token=" + token, Usuario.class
        );

        return response.getBody();
    }

    public List<Usuario> registroUsuario() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Usuario[]> response = restTemplate.getForEntity(
                "http://localhost:8080/category", Usuario[].class);

        return Arrays.asList(response.getBody());
    }



}
