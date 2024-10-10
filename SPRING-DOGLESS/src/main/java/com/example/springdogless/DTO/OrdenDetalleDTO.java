package com.example.springdogless.DTO;

import java.math.BigDecimal;
import java.sql.Date;

public interface OrdenDetalleDTO {
     int getIdordenes();;
     String getEstado();;
     Date getFecha();
     String getDireccionenvio();
     BigDecimal getTotal();
     String getMetodopago();
     String getNombreproducto();
     Integer getCantidad();
     BigDecimal getSubtotal();
}

