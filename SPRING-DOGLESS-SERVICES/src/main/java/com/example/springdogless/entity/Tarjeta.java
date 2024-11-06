package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity(name="tarjetas")
@Getter
@Setter
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtarjetas")
    private Integer id;
    private String numero;
    private String tipo;
    private String cvv;
    private Date fecha;

}
