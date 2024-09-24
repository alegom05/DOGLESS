package com.example.holamundo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity (name="distritos")
@Getter
@Setter
public class Distrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer iddistritos;

    @Column (name="nombre")
    private String distrito;

    @ManyToOne
    @JoinColumn(name = "idzonas")
    private Zona zona;
}
