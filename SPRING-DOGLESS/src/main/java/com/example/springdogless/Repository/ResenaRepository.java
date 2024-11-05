package com.example.springdogless.Repository;

import com.example.springdogless.DTO.ResenaDTO;
import com.example.springdogless.entity.Producto;
import com.example.springdogless.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Integer> {
    @Query(value = "SELECT " +
            "r.idresenas, " +
            "r.comentario, " +
            "r.satisfaccion, " +
            "r.fecha, " +
            "u.idusuarios, " +
            "u.nombre AS usuarioNombre, " +
            "u.apellido AS usuarioApellido, " +
            "p.idproductos, " +
            "p.nombre AS productoNombre, " +
            "r.tipo " +
            "FROM dogless.resenas r " +
            "JOIN dogless.usuarios u ON r.usuarioid = u.idusuarios " +
            "JOIN dogless.productos p ON r.idproductos = p.idproductos " +
            "WHERE r.idproductos = ?1 and r.tipo= ?2",  // El ID del producto es el primer parámetro
            nativeQuery = true)
    List<ResenaDTO> findResenasByProductoId(Integer idProducto, Integer tipo);
}
