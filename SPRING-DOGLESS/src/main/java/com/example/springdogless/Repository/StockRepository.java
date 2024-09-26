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
    @Query(value = "UPDATE dogless.stockproductos SET cantidad = cantidad + :cantidad_reposi WHERE productoid = :idproducto AND idzonas = :idzonas", nativeQuery = true)
    void actualizarStock(@Param("cantidad_reposi") int cantidadAprobada, @Param("idproducto") int idProducto, @Param("idzonas") int idZona);
}
