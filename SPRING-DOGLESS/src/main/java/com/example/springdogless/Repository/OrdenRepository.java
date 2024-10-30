package com.example.springdogless.Repository;

import com.example.springdogless.DTO.OrdenEstadoDTO;
import com.example.springdogless.entity.Orden;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
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



    @Query(value = "SELECT " +
            "CASE " +
            "WHEN o.estado IN ('En Proceso', 'Arribo al País', 'En Aduanas', 'En Ruta', 'Recibido') THEN 'Procesando' " +
            "WHEN o.estado IN ('Creado', 'En Validación') THEN 'No procesadas' " +
            "END AS categoria, " +
            "COUNT(*) AS cantidad " +
            "FROM Ordenes o " +
            "WHERE o.estado IN ('En Proceso', 'Arribo al País', 'En Aduanas', 'En Ruta', 'Recibido', 'Creado', 'En Validación') " +
            "GROUP BY categoria", nativeQuery = true)
    List<OrdenEstadoDTO> contarOrdenesPorProceso();




    @Query(value = "SELECT o.estado AS estado, COUNT(o.estado) AS cantidad " +
            "FROM ordenes o " +
            "GROUP BY o.estado", nativeQuery = true)
    List<OrdenEstadoDTO> contarOrdenesPorEstado();




    @Query(value =
            "WITH Meses AS ( " +
                    "    SELECT 1 AS mes_numero, 'Enero' AS mes " +
                    "    UNION ALL SELECT 2, 'Febrero' " +
                    "    UNION ALL SELECT 3, 'Marzo' " +
                    "    UNION ALL SELECT 4, 'Abril' " +
                    "    UNION ALL SELECT 5, 'Mayo' " +
                    "    UNION ALL SELECT 6, 'Junio' " +
                    "    UNION ALL SELECT 7, 'Julio' " +
                    "    UNION ALL SELECT 8, 'Agosto' " +
                    "    UNION ALL SELECT 9, 'Septiembre' " +
                    "    UNION ALL SELECT 10, 'Octubre' " +
                    "    UNION ALL SELECT 11, 'Noviembre' " +
                    "    UNION ALL SELECT 12, 'Diciembre' " +
                    ") " +
                    "SELECT " +
                    "    m.mes, " +
                    "    COALESCE(SUM(CASE WHEN o.categoria = 'Órdenes Procesadas' THEN o.cantidad END), 0) AS cantidad_procesadas, " +
                    "    COALESCE(SUM(CASE WHEN o.categoria = 'Órdenes Canceladas' THEN o.cantidad END), 0) AS cantidad_canceladas " +
                    "FROM Meses m " +
                    "LEFT JOIN ( " +
                    "    SELECT " +
                    "        CASE " +
                    "            WHEN o.estado = 'Recibido' THEN 'Órdenes Procesadas' " +
                    "            WHEN o.estado = 'Cancelado' THEN 'Órdenes Canceladas' " +
                    "        END AS categoria, " +
                    "        MONTH(o.fecha) AS mes_numero, " +
                    "        COUNT(*) AS cantidad " +
                    "    FROM Ordenes o " +
                    "    WHERE o.estado IN ('Recibido', 'Cancelado') " +
                    "    GROUP BY categoria, mes_numero " +
                    ") o ON m.mes_numero = o.mes_numero " +
                    "GROUP BY m.mes_numero, m.mes " +
                    "ORDER BY m.mes_numero;", nativeQuery = true)
    List<OrdenEstadoDTO> contarOrdenesProcesadasYCanceladasPorMes();


}
