package com.example.springdogless.DTO;


public interface ProductoDTO {
    int getIdproductos();
    String getNombre();
    String getDescripcion();
    String getCategoria();
    Double getPrecio();
    Double getCostoenvio();
    String getModelos();
    String getColores();
    String getAprobado();
    Integer getBorrado();
    String getEstado();
    Integer getCantidad();
    String getNombreZona();
    Integer getIdzonas();

    // Nuevos métodos para proveedor
    String getProveedorNombre();
    String getProveedorApellido();

    Integer getPromedioSatisfaccion();

    Integer getTotal_vendido();
}
