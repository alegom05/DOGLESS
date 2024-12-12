package com.example.springdogless.config;

import com.example.springdogless.Repository.FotosPerfilBlobsRepository;
import com.example.springdogless.Repository.ProductRepository;
import com.example.springdogless.Repository.UsuarioRepository;
import com.example.springdogless.entity.FotosPerfilBlobs;
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
    @Autowired
    private FotosPerfilBlobsRepository fotosPerfilBlobsRepository;

    @GetMapping("/imagen/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Integer id) {
        System.out.println("Llamando a getImage con ID: " + id); // Agrega este log
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            // Buscar la foto de perfil asociada al usuario
            Optional<FotosPerfilBlobs> fotoPerfilBlob = fotosPerfilBlobsRepository.findByUsuario_Id(usuario.getId());

            byte[] imagenBytes = null;

            if (fotoPerfilBlob.isPresent() && fotoPerfilBlob.get().getFotoPerfil() != null) {
                // Si el usuario tiene una foto, usa esa foto
                imagenBytes = fotoPerfilBlob.get().getFotoPerfil();
            } else {
                System.out.println("El usuario no tiene una foto de perfil asignada, intentando cargar la imagen por defecto...");
                // Buscar la foto por defecto del usuario con ID 59
                Optional<FotosPerfilBlobs> fotoDefault=fotosPerfilBlobsRepository.findByUsuario_Id(0);
                if (fotoDefault.isPresent() && fotoDefault.get().getFotoPerfil() != null) {
                    imagenBytes = fotoDefault.get().getFotoPerfil();
                } else {
                    System.out.println("No se encontró una foto de perfil por defecto válida.");
                }
            }

            if (imagenBytes != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imagenBytes);
            }

            // Si no se tiene ni la foto del usuario ni una foto por defecto
            System.out.println("No se encontró imagen para el usuario con ID: " + id);
            return ResponseEntity.noContent().build();
        }

        System.out.println("No se encontró usuario con ID: " + id); // Agrega este log
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
