package com.example.springdogless.APIdni;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DniResponse {
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String apellidosCompletos; // Nuevo campo
}
