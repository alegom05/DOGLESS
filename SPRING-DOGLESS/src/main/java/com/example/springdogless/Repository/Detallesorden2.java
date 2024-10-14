package com.example.springdogless.Repository;

import com.example.springdogless.DTO.OrdenDetalleDTO;
import com.example.springdogless.entity.Detalleorden;
import com.example.springdogless.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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


    @Query(value = "SELECT o.idordenes, o.total, p.nombre AS nombre_producto," +
            " p.idproductos,p.precio, p.costoenvio, do.cantidad, do.subtotal " +
            "FROM ordenes o " +
            "JOIN usuarios u ON o.idusuarios = u.idusuarios " +
            "JOIN detallesorden do ON o.idordenes = do.idorden " +
            "JOIN productos p ON do.idproducto = p.idproductos " +
            "WHERE o.idusuarios = :id AND o.estado = 'CREADO'", nativeQuery = true)
    List<Object[]> findOrderCreada(@Param("id") Integer id);



    @Query(value = "SELECT do.*\n" +
            "FROM detallesorden do\n" +
            "JOIN ordenes o ON do.idorden = o.idordenes\n" +
            "WHERE o.idusuarios = :id AND o.estado = 'CREADO'",nativeQuery = true)
    List<Detalleorden> findByOrdenCreado(@Param("id") Integer id);

    @Query(value = "SELECT do.*\n" +
            "FROM detallesorden do\n" +
            "JOIN ordenes o ON do.idorden = o.idordenes\n" +
            "WHERE o.idusuarios = :id AND o.estado = 'En Validación'",nativeQuery = true)
    List<Detalleorden> findByOrdenValidada(@Param("id") Integer id);

}
