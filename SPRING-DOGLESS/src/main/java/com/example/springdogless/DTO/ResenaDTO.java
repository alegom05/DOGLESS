package com.example.springdogless.DTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public interface ResenaDTO {
    int getIdUsuarios();
    int getIdResenas();
    String getComentario();
    Integer getSatisfaccion();
    Date getFecha();
    String getUsuarioNombre();
    String getUsuarioApellido();
    int getIdProductos();
    String getProductoNombre();

}
