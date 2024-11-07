package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name="mensajes")
@Getter
@Setter
public class Mensaje {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idmensajes")
    private Integer idmensajes;

    private String texto;

    @ManyToOne
    @JoinColumn(name = "idusuarios")
    private Usuario usuario;



}
