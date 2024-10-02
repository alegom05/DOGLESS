package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Getter
@Setter
@Table(name = "detallesorden")
public class Detalleorden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer iddetallesorden;

    private Integer cantidad;
    private BigDecimal preciounitario;
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "idorden", nullable = false)
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "idproducto", nullable = false)
    private Producto producto;

    // getters y setters

}
