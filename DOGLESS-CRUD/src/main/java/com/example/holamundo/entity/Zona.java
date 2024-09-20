package com.example.holamundo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idzonas;

    private String nombre;

}
