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

    @ManyToOne
    @JoinColumn(name = "idusuarios", nullable = false)
    private Usuario nombre;

    @ManyToOne
    @JoinColumn(name = "idusuariosapellido", nullable = false)
    private Usuario apellido;

    @ManyToOne
    @JoinColumn(name = "idusuarioscodigoad", nullable = false)
    private Usuario codigoaduana;

    @ManyToOne
    @JoinColumn(name = "idusuariosestado", nullable = false)
    private Usuario estado;

    @ManyToOne
    @JoinColumn(name = "idusuarioscodigojur", nullable = false)
    private Usuario codigojurisdiccion;


}
