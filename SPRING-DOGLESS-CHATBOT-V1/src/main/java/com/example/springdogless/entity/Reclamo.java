package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name="reclamos")
@Getter
@Setter
public class Reclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idreclamos")
    private Integer id;

    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "idusuarios")
    private Usuario usuario;

}
