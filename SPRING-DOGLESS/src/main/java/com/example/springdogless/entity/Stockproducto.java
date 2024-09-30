package com.example.springdogless.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class Stockproducto implements Serializable{


    @Column(name = "idproductos", nullable = false)
    private Integer idproductos;

    @Column(name = "idzonas", nullable = false)
    private Integer idzonas;

    private Integer cantidad;

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

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
