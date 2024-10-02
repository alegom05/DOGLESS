package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Getter
@Setter
public class Importacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idimportaciones;

    private String cantidad;

    @Column(name = "fecha_pedido")
    private Date fechaPedido;

    private String aprobar;

    private Integer borrado;

    @ManyToOne
    @JoinColumn(name = "idzonas")
    private Zona zona;

    @ManyToOne
    @JoinColumn(name = "idproductos")
    private Producto producto;
}
