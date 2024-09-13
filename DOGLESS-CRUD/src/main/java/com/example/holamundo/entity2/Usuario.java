package com.example.holamundo.entity2;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idusuarios;
    private String nombre;
    private String apellido;
    private String dni;
    private String correo;
    private String telefono;
    private String direccion;

    @ManyToOne
    private Rol rol;

    private Integer iddistritos;

    private Integer idadminzonales;

    private Integer estado;
    private String ruc;
    private String codigoaduana;
    private String razonsocial;
    private String codigojurisdiccion;
    private Integer idzonas;
}
