package com.example.springdogless.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
public class RegistroUsuarioDTO {
     private String nombre;
     private String apellido;
     private String email;
     private String password;
     private String direccion;
     private Integer idDistrito;
     private String pwd;


     // Changed to receive just the ID
     // Add other necessary fields...
}

