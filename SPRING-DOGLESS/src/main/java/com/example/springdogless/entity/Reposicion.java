package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity(name="reposicion")
@Getter
@Setter
public class Reposicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreposicion")
    private Integer id;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "fecha_pedido")
    private Date fecha_pedido;

    @Column(name = "aprobar", length = 45)
    private String aprobar;

    @ManyToOne
    @JoinColumn(name = "idproductos", nullable = false)
    private Producto producto;

    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn(name = "idzonas")
    private Zona zona;

    private Integer borrado;
}
