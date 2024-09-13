package com.example.holamundo.entity2;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idusuarios;
    private String nombre;
    private String apellido;
    private String dni;
    private String correo;
    private String telefono;
    private String direccion;


    private Integer idroles;

    private Integer iddistritos;

    private Integer idadminzonales;

    private Integer estado;
    private String ruc;
    private String codigoaduana;
    private String razonsocial;
    private String codigojurisdiccion;
    private Integer idzonas;
}
