package com.example.springdogless.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TarjetaRequest {
    private String numero;
    private int cvv;
    private String fecha;
}
