package com.example.springdogless.Repository;

import com.example.springdogless.entity.Orden;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Integer> {

    List<Orden> findByBorrado(Integer borrado);

    @Query(value="select * from ordenes where ordenes.idordenes=:id",nativeQuery = true)
    Orden findByIdOrden(@Param("id") Integer id);

    @Query(value="SELECT o.*, p.idproductos,p.nombre,p.precio,p.costoenvio,d.cantidad, d.subtotal FROM ordenes o JOIN detallesorden d ON o.idordenes = d.idorden JOIN productos p ON d.idproducto = p.idproductos WHERE o.idordenes = :id", nativeQuery = true)
    Optional<Orden> findByIdWithDetails(@Param("id") Integer id);

    @Modifying
    @Query(value="UPDATE ordenes SET estado = :nuevoEstado WHERE idordenes = :idOrden", nativeQuery = true)
    int actualizarEstado(@Param("idOrden") int idOrden, @Param("nuevoEstado") String nuevoEstado);

    @Query(value="select * from ordenes where ordenes.estado='CREADO' and ordenes.idusuarios=:id",nativeQuery = true)
    Optional<Orden> findOrdenCreado(@Param("id") Integer id);

    @Query(value = "SELECT * FROM ordenes WHERE estado = 'CREADO'", nativeQuery = true)
    List<Orden> findOrdenesSinAsignar();

    @Query(value = "SELECT * FROM ordenes WHERE estado = 'En Validación'", nativeQuery = true)
    List<Orden> findOrdenesEnValidacion();

    @Query(value = "SELECT * FROM ordenes WHERE estado IN ('En Proceso', 'Arribo al País', 'En Aduanas', 'En Ruta')", nativeQuery = true)
    List<Orden> findOrdenesEnProgreso();

    @Query(value = "SELECT * FROM ordenes WHERE estado IN ('Recibido', 'Cancelado')", nativeQuery = true)
    List<Orden> ordenesResueltas();

    @Query(value="SELECT * FROM ordenes WHERE idusuarios = :id AND estado = 'Creado'",nativeQuery = true)
    Orden findOrdenEstadoCreado(Integer id);







}
