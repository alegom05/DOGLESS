package com.example.springdogless.entity;
import org.springframework.web.multipart.MultipartFile;

public class ProductoForm {

    // Otros campos del formulario que quieras manejar
    private String nombre;
    private String descripcion;
    private double precio;
    private String categoria;
    private Double costoenvio;
    private String modelos;
    private String colores;
    private int idproveedor;

    // Campo para la imagen
    private MultipartFile imagenprod;

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public MultipartFile getImagenprod() {
        return imagenprod;
    }

    public void setImagenprod(MultipartFile imagenprod) {
        this.imagenprod = imagenprod;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getCostoenvio() {
        return costoenvio;
    }

    public void setCostoenvio(Double costoenvio) {
        this.costoenvio = costoenvio;
    }

    public String getModelos() {
        return modelos;
    }

    public void setModelos(String modelos) {
        this.modelos = modelos;
    }

    public String getColores() {
        return colores;
    }

    public void setColores(String colores) {
        this.colores = colores;
    }

    public int getIdproveedor() {
        return idproveedor;
    }

    public void setIdproveedor(int idproveedor) {
        this.idproveedor = idproveedor;
    }
}

