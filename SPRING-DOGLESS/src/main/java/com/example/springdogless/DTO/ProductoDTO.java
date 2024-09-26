package com.example.springdogless.DTO;

public interface ProductoDTO {
    int getIdproductos();
    String getNombre();
    String getDescripcion();
    String getCategoria();
    double getPrecio();
    double getCostoenvio();
    String getModelos();
    String getColores();
    String getAprobado();
    int getBorrado();
    String getEstado();
    int getCantidad();
    String getNombreZona();

    // Nuevos métodos para proveedor
    String getProveedorNombre();
    String getProveedorApellido();
}
