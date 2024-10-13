package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity(name="reportes")
@Getter
@Setter
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreportes")
    private int idReportes;

    @Column(name = "nombre", length = 45)
    private String nombre;

    @Column(name = "tipo", length = 45)
    private String tipo;

    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "idusuarios")
    private Usuario usuario;

}
