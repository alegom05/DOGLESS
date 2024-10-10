package com.example.springdogless.Repository;

import com.example.springdogless.DTO.OrdenDetalleDTO;
import com.example.springdogless.entity.Detalleorden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Detallesorden2 extends JpaRepository<Detalleorden, Integer> {
    @Query(value = "SELECT o.idordenes, o.estado, o.fecha, o.direccionenvio, o.total, o.metodopago, " +
            "p.nombre AS nombre_producto, do.cantidad, do.subtotal " +
            "FROM ordenes o " +
            "JOIN usuarios u ON o.idusuarios = u.idusuarios " +
            "JOIN detallesorden do ON o.idordenes = do.idorden " +
            "JOIN productos p ON do.idproducto = p.idproductos " +
            "WHERE o.idusuarios = :idUsuario", nativeQuery = true)
    List<Object[]> findOrdersByUserId(@Param("idUsuario") Integer idUsuario);

}
