package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity (name="Solicitudes")
@Getter
@Setter
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idsolicitudes", nullable = false)
    private Integer id;

    private Byte veredicto;

    @Column(name = "comentario", length = 300)
    private String comentario;

    @Column(name = "idusuarios", nullable = false)
    private Integer idusuarios;



}
