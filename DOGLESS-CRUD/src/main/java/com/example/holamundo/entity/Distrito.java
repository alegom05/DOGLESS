package com.example.holamundo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Distrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer iddistritos;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "idzonas")
    private Zona zona;
}
