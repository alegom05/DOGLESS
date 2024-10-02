package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Entity (name="ordenes")
@Getter
@Setter
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idordenes")
    private Integer id;

    @Column
    private String estado;

    @Column
    private Date fecha;

    @Column
    private String direccionenvio;

    @Column
    private BigDecimal total;

    @Column
    private String metodopago;

    private Integer borrado;

    @ManyToOne
    @JoinColumn(name = "idusuarios", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    private List<Detalleorden> detalleOrden;



    /*
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetallesOrden> detallesOrden;
    */
}
