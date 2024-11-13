package com.example.springdogless.Repository;

import com.example.springdogless.entity.Reposicion;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Reposicion, Integer> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE dogless.stockproductos SET cantidad = cantidad + :cantidad_reposi WHERE idproductos = :idproducto AND idzonas = :idzonas", nativeQuery = true)
    void actualizarStock(@Param("cantidad_reposi") int cantidadAprobada, @Param("idproducto") int idProducto, @Param("idzonas") int idZona);

    // Verifica si el producto ya existe en la tabla stockproductos para una zona específica
    @Query(value = "SELECT CASE \n" +
            "    WHEN COUNT(*) > 0 THEN 1 \n" +
            "    ELSE 0 \n" +
            "END AS existe \n" +
            "FROM dogless.stockproductos \n" +
            "WHERE idproductos = :productoid \n" +
            "AND idzonas = :idzonas", nativeQuery = true)
    Integer existeProductoEnZona(@Param("productoid") Integer productoid, @Param("idzonas") Integer idzonas);

    // Inserta un nuevo registro en la tabla stockproductos
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO stockproductos (idproductos, idzonas, cantidad) VALUES (:productoid, :idzonas, :cantidad)", nativeQuery = true)
    void insertarNuevoStock(@Param("productoid") Integer productoid, @Param("idzonas") Integer idzonas, @Param("cantidad") Integer cantidad);

}
