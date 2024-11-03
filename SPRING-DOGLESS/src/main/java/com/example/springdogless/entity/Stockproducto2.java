package com.example.springdogless.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class Stockproducto2 implements Serializable {
    @Column(name = "idproductos", nullable = false)
    private Integer idproductos;

    @Column(name = "idzonas", nullable = false)
    private Integer idzonas;

    // Getters y Setters
    public Integer getIdproductos() {
        return idproductos;
    }

    public void setIdproductos(Integer idproductos) {
        this.idproductos = idproductos;
    }

    public Integer getIdzonas() {
        return idzonas;
    }

    public void setIdzonas(Integer idzonas) {
        this.idzonas = idzonas;
    }
}