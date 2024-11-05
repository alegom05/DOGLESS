package com.example.springdogless.APIdni;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/registro")
public class DniController {

    private static final String API_URL = "https://api.apis.net.pe/v2/reniec/dni";
    private static final String API_TOKEN = "apis-token-11361.wuV88f3H0CDNySuLXqYUtr5mluWywes0";  // Reemplaza con tu token real

    private final RestTemplate restTemplate;

    public DniController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/consultar-dni")
    public ResponseEntity<?> consultarDni(@RequestParam String dni) {
        String url = API_URL + "?numero=" + dni;

        // Configurar headers con el token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Realizar la solicitud a la API
            ResponseEntity<DniResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, DniResponse.class);
            DniResponse dniData = response.getBody();

            // Concatenar apellidos
            if (dniData != null) {
                String apellidosCompletos = dniData.getApellidoPaterno() + " " + dniData.getApellidoMaterno();
                dniData.setApellidosCompletos(apellidosCompletos); // Agregar campo concatenado
            }

            // Retornar la respuesta si es exitosa
            return ResponseEntity.ok(dniData);

        } catch (HttpClientErrorException e) {
            // Manejar errores HTTP 422
            if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("El DNI no cumple con las reglas de validación.");
            }
            // Manejar otros errores
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al consultar el DNI.");
        }
    }
}