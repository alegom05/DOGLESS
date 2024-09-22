package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Adminzonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idadminzonales;
    private String nombre;
    private String apellido;
    @Column(length = 8)
    private String dni;
    private String telefono;
    private String email;
    private String clave;

    @ManyToOne
    @JoinColumn(name="idzonas")
    private Zona Zona;
}
