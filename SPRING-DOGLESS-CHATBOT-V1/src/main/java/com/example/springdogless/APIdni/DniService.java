package com.example.springdogless.APIdni;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class DniService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://api.apis.net.pe/v2/reniec/dni";

    public DniResponse consultarDni(String dni) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("numero", dni)
                .toUriString();

        try {
            return restTemplate.getForObject(url, DniResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 422) {
                throw new IllegalArgumentException("El DNI no cumple con las reglas de validación.");
            }
            throw new RuntimeException("Error al consultar el DNI.");
        }
    }
}
