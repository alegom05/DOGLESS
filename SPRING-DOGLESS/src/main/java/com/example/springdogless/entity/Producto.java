package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idproductos;

    private String nombre;
    private String descripcion;
    private String categoria;
    private BigDecimal precio;
    private BigDecimal costoenvio;
    private String modelos;
    private String colores;

    @ManyToOne
    @JoinColumn(name = "proveedorid", nullable = false)
    private Proveedor proveedor;

}
