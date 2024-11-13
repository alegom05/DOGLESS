package com.example.springdogless.controllers;

import com.example.springdogless.Repository.ProductRepository;
import com.example.springdogless.entity.Producto;
import com.example.springdogless.services.BoletaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BoletaController {

    @Autowired
    private ProductRepository productoRepository;

    @Autowired
    private BoletaService boletaService;

    @GetMapping("/boleta/{productoId}")
    public ResponseEntity<Resource> descargarBoleta(@PathVariable Integer productoId) {
        try {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            ByteArrayResource resource = boletaService.generarBoletaPDF(producto);

            return ResponseEntity.ok()
                    .headers(headers -> headers.add(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=boleta_producto_" + productoId + ".pdf"))
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace(); // Para debugging
            return ResponseEntity.internalServerError().build();
        }
    }
}