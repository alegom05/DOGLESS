package com.example.springdogless.config;

import com.example.springdogless.Repository.ProductRepository;
import com.example.springdogless.Repository.UsuarioRepository;
import com.example.springdogless.entity.Producto;
import com.example.springdogless.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Controller
@RequestMapping({""})
public class ImageController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

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

    @GetMapping("/producto/imagen/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getProductImage(@PathVariable("id") Integer id) {
        System.out.println("Llamando a getImage con ID: " + id); // Agrega este log
        Optional<Producto> productoOptional = productRepository.findById(id);

        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            byte[] imagenBytes = producto.getImagenprod();

            // Log para verificar la longitud de la imagen
            System.out.println("Imagen encontrada para ID: " + id + ", longitud de imagen en bytes: " + imagenBytes.length);

            // Configuración de tipo de contenido
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // o MediaType.IMAGE_PNG según sea el caso
                    .body(imagenBytes);
        }

        System.out.println("No se encontró producto con ID: " + id); // Agrega este log
        return ResponseEntity.notFound().build();
    }

}
