package com.example.springdogless.Repository;

import com.example.springdogless.DTO.ProductoDTO;
import com.example.springdogless.DTO.ProductoImportadoDTO;
import com.example.springdogless.entity.Importacion;
import com.example.springdogless.entity.Orden;
import com.example.springdogless.entity.Reposicion;
import com.example.springdogless.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImportacionRepository extends JpaRepository<Importacion, Integer> {
    Optional<Importacion> findById(Integer idimportaciones);
    List<Importacion> findByBorrado(Integer borrado);
    @Query(value = "SELECT p.nombre AS nombre_producto, " +
            "SUM(i.cantidad) AS total_importado " +
            "FROM dogless.importaciones AS i " +
            "JOIN dogless.productos AS p ON i.idproductos = p.idproductos " +
            "WHERE i.idzonas = :idZona " +
            "GROUP BY i.idproductos, p.nombre " +
            "ORDER BY total_importado DESC",
            nativeQuery = true)
    List<ProductoImportadoDTO> topProductosImportados(@Param("idZona") Integer idZona);
}
