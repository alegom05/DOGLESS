package com.example.springdogless.controllers;


import com.example.springdogless.Repository.TarjetaRepository;
import com.example.springdogless.entity.Tarjeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class TarjetaController {

    @Autowired
    TarjetaRepository tarjetaRepository;

    @ResponseBody
    @GetMapping(value="/tarjeta",produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public List<Tarjeta> listaTarjetas() {
        return tarjetaRepository.findAll();
    }

    @GetMapping(value = "/tarjeta/{id}")
    public ResponseEntity<HashMap<String, Object>> obtenerProductoPorId(@PathVariable("id") String idStr) {
        HashMap<String, Object> responseJson = new HashMap<>();
        try {
            Optional<Tarjeta> optTarjeta = tarjetaRepository.findById(Integer.parseInt(idStr));
            if (optTarjeta.isPresent()) {
                responseJson.put("result", "success");
                responseJson.put("tarjeta", optTarjeta.get());
                return ResponseEntity.ok(responseJson);
            } else {
                responseJson.put("msg", "Producto no encontrado");
            }
        } catch (NumberFormatException e) {
            responseJson.put("msg", "El ID debe ser un número entero positivo");
        }
        responseJson.put("result", "failure");
        return ResponseEntity.badRequest().body(responseJson);
    }


}
