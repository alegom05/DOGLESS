package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity(name="preguntasproducto")
@Getter
@Setter
public class Preguntasproducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpreguntasproducto")
    private Integer idpreguntas;

    @Column
    private String comentario;
    @Column
    private LocalDate fecha;

    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn(name = "idusuarios", nullable = false)
    private Usuario usuario;

    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn(name = "idproductos", nullable = false)
    private Producto producto;
    
}
