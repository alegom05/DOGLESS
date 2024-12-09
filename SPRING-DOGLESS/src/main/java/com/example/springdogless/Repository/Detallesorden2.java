package com.example.springdogless.Repository;

import com.example.springdogless.entity.Detalleorden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    @Query(value = "SELECT * FROM detallesorden WHERE idorden = :idOrden AND idproducto = :idProducto", nativeQuery = true)
    Optional<Detalleorden> findByIdordenAndIdproducto(@Param("idOrden") Integer idOrden, @Param("idProducto") Integer idProducto);


    @Modifying
    @Query(value="DELETE FROM DetallesOrden d WHERE d.orden.id = :idOrden AND d.producto.id = :idProducto",nativeQuery = true)
    Optional<Detalleorden>  deleteByOrdenIdAndProductoId(@Param("idOrden") Integer idOrden, @Param("idProducto") Integer idProducto);

    @Query(value="SELECT * FROM detallesorden WHERE iddetallesorden = :idDetallesOrden", nativeQuery = true)
    Optional<Detalleorden> findByIdDetalle(Integer idDetallesOrden);


    @Query(value="SELECT * FROM detallesorden WHERE idorden=:id", nativeQuery = true)
    List<Detalleorden> findListaDetallesOrdenes(@Param("id") Integer id);

    List<Detalleorden> findAllByOrdenId(Integer id);
}
