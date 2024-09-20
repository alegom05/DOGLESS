package com.example.holamundo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity (name="usuarios")
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
    @JoinColumn(name = "idroles")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "iddistritos")
    private Distrito distrito;

    private Integer idadminzonales;

    private String estado;
    private String ruc;
    private String codigoaduana;
    private String razonsocial;
    private String codigojurisdiccion;
    private Integer idzonas;
}
