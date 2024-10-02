package com.example.springdogless.Repository;

import com.example.springdogless.entity.Orden;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Integer> {

    List<Orden> findByBorrado(Integer borrado);

    Optional<Orden> findById(Integer id);

    @Query(value="SELECT o.*, p.idproductos,p.nombre,p.precio,p.costoenvio,d.cantidad, d.subtotal FROM ordenes o JOIN detallesorden d ON o.idordenes = d.idorden JOIN productos p ON d.idproducto = p.idproductos WHERE o.idordenes = :id", nativeQuery = true)
    Optional<Orden> findByIdWithDetails(@Param("id") Integer id);
}
