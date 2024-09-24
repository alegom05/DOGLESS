package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity (name="productos")
@Getter
@Setter
public class Producto {

    @Id
    @Column (name="idproductos")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String descripcion;
    private String categoria;
    private BigDecimal precio;
    private BigDecimal costoenvio;
    private String modelos;
    private String colores;

    @ManyToOne
    @JoinColumn(name = "idproveedores", nullable = false)
    private Proveedor proveedor;

}
