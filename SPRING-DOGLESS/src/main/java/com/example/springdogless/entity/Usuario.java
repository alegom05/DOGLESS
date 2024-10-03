package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity (name="usuarios")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="idusuarios")
    private Integer id;
    private String nombre;
    private String apellido;
    private String dni;
    private String correo;
    private String telefono;
    private String direccion;
    private String contrasena;
    private Integer borrado;
    private Date fechabaneo;
    private String motivobaneo;


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
    @ManyToOne
    @JoinColumn(name = "idzonas")
    private Zona zona;
    private String fechanacimiento;

    public void setFechabaneo(LocalDate now) {
    }
}
