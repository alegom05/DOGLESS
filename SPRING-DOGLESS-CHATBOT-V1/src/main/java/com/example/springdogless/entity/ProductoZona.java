package com.example.springdogless.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "stockproductos")
public class ProductoZona implements Serializable {

    @EmbeddedId
    private Stockproducto2 stockproducto;

    @ManyToOne
    @MapsId("idproductos")  // Mapea el idproductos en Stockproducto con Producto
    @JoinColumn(name = "idproductos", insertable = false, updatable = false)
    private Producto producto;


    @ManyToOne
    @MapsId("idzonas")  // Mapea el idzonas en Stockproducto con Zona
    @JoinColumn(name = "idzonas", insertable = false, updatable = false)
    private Zona zona;
    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "borrado")
    private Integer borrado;


    // Getters y Setters
    public Stockproducto2 getStockproducto() {
        return stockproducto;
    }

    public void setStockproducto(Stockproducto2 stockproducto) {
        this.stockproducto = stockproducto;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getBorrado() {
        return borrado;
    }

    public void setBorrado(Integer borrado) {
        this.borrado = borrado;
    }
}
