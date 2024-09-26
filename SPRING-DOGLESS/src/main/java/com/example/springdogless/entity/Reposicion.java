package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity(name="reposicion")
@Getter
@Setter
public class Reposicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreposicion")
    private Integer idreposicion;

    @Column(name = "nombre_producto", length = 45)
    private String nombre_producto;

    @Column(name = "cantidad", length = 45)
    private String cantidad;

    @Column(name = "fecha_pedido")
    private Date fecha_pedido;

    @Column(name = "aprobar", length = 45)
    private String aprobar;

    @ManyToOne
    @JoinColumn(name = "productos_idproductos", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "zonas_idzonas", nullable = false)
    private Zona zona;
}
