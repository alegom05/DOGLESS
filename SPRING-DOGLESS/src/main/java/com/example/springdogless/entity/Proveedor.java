package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity (name="proveedores")
@Getter
@Setter
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproveedores")
    private Integer id;

    @Column(name = "nombre", length = 45)
    private String nombre;

    @Column(name = "apellido", length = 45)
    private String apellido;

    @Column(name = "telefono", length = 45)
    private String telefono;

    @Column(name = "ruc", length = 11)
    private String ruc;

    @Column(name = "dni", length = 8)
    private String dni;

    @Column(name = "tienda", length = 45)
    private String tienda;

    @Column(name = "estado")
    private String estado;


}
