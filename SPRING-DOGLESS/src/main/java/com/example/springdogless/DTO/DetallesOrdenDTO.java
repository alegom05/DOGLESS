package com.example.springdogless.DTO;

import java.math.BigDecimal;

public interface DetallesOrdenDTO {
    Integer getIdProductos();
    String getNombre();
    Integer getCantidad();
    BigDecimal getPrecioUnitario();
    BigDecimal getsubtotal();
}
