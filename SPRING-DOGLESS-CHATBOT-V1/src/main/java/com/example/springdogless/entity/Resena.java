package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity(name="resenas")
@Getter
@Setter
public class Resena {
    @Id
    @Column(name="idresenas")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String comentario;
    private Date fecha;
    private Integer satisfaccion;
    private Integer atencion;
    private Integer calidad;
    private Integer tipo;
    private Integer serecibiorapido;
    private String aprobar;

    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn(name = "idproductos", nullable = false)
    private Producto producto;

    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn(name = "usuarioid", nullable = false)
    private Usuario usuario;
}
