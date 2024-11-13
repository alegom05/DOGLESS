package com.example.springdogless.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity (name="productos")
@Getter
@Setter
public class Producto {

    @Id
    @Column (name="idproductos")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String descripcion;
    private String categoria;

    /*
    @Digits(integer = 10, fraction = 1, message = "Puede tener hasta un dígito decimal")
    @Max(value = 15)
    @Min(value = 0)
    */
    private Double precio;
    private Double costoenvio;
    private String modelos;
    private String colores;
    private Integer borrado;
    private String estado;
    private String aprobado;


    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn(name = "idproveedores", nullable = false)
    private Proveedor proveedor;

    @Lob
    @Column(name = "imagenprod", columnDefinition = "BLOB")
    private byte[] imagenprod;  // Cambiado a byte[] para el campo BLOB


}
