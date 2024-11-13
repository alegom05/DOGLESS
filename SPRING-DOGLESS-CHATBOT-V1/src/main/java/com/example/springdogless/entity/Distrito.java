package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity (name="distritos")
@Getter
@Setter
public class Distrito implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer iddistritos;

    @Column (name="nombre")
    private String distrito;

    @ManyToOne
    @JoinColumn(name = "idzonas")
    private Zona zona;
}
