package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity (name="importaciones")
@Getter
@Setter
public class Importacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idimportaciones")
    private Integer id;
    /*

    <button type="submit" class="btn btn-primary" style="width: 280px" th:text="${reposicion.id==0? 'Crear':'Actualizar'>Crear</button>

     */
    private String cantidad;

    @Column(name = "fecha_pedido")
    private Date fechaPedido;

    private String aprobar;

    private Integer borrado;

    @ManyToOne
    @JoinColumn(name = "idzonas")
    private Zona zona;

    @ManyToOne
    @JoinColumn(name = "idproductos")
    private Producto producto;
}
