package com.example.springdogless.Repository;

import com.example.springdogless.DTO.*;

import com.example.springdogless.entity.Orden;
import com.example.springdogless.entity.Rol;
import com.example.springdogless.entity.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    @Query(nativeQuery = true, value = "select * from usuarios where idroles like %?1% and borrado = 1")
    List<Usuario> findByRol(Integer idRol);

    List<Usuario> findByRol_RolAndBorrado(String rol, Integer borrado);

    @Query(nativeQuery = true, value = "SELECT COUNT(o.idordenes)\n" +
            "FROM usuarios u\n" +
            "LEFT JOIN ordenes o ON u.idusuarios = o.idusuarios\n" +
            "GROUP BY u.idusuarios;")
    List<Usuario> findNumberOfOrders(Integer ordenes);
    // Metodo para buscar usuarios por estado (activo, inactivo, baneado)
    List<Usuario> findByEstado(String estado);

    // Metodo para buscar usuarios baneados
    @Query(nativeQuery = true, value = "SELECT * FROM usuarios WHERE estado = 'baneado' AND borrado = 1")
    List<Usuario> findBaneados();

    // Metodo para buscar usuarios activos
    @Query(nativeQuery = true, value = "SELECT * FROM usuarios WHERE estado = 'activo' AND borrado = 1")
    List<Usuario> findActivos();

    @Query(nativeQuery = true, value = "SELECT * FROM usuarios WHERE idroles = ?1 AND borrado = 1 AND estado <> 'baneado'")
    List<Usuario> findByRolAndNotBaneado(Integer idRol);

    public Usuario findByEmail(String email);
    Usuario findByCodigoaduana(String codigoAduana);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update usuarios u set u.estado = 'baneado', u.motivobaneo = ?2 where u.idUsuarios = ?1")
    void banear(Integer idUsuario,String motivoBaneo);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update usuarios u set u.estado = 'activo' where u.idUsuarios = ?1")
    void desbanear(Integer idUsuario);

    Optional<Usuario> findById(Integer id); // Esto usa la implementación por defecto


    List<Usuario> findByBorradoAndRol_Rol(Integer borrado, String rol);
    @Query(value = """
                    SELECT 
                        u.nombre,
                        SUM(d.cantidad) AS totalCantidad
                    FROM 
                        ordenes AS o
                    JOIN 
                        detallesorden AS d ON o.idordenes = d.idorden
                    JOIN 
                        usuarios AS u ON o.idusuarios = u.idusuarios
                    GROUP BY 
                        u.nombre;
                    """, nativeQuery = true)
    List<UsuarioCantidadDTO> obtenerTotalCantidadPorUsuario();

    //Métodos del Dashboard
    @Query("SELECT COUNT(u) FROM usuarios u WHERE u.rol.id = 3")
    Integer contarAgentes();

    @Query("SELECT COUNT(*) FROM usuarios u WHERE u.estado = 'baneado'")
    Integer contarBaneados();

    @Query("SELECT COUNT(*) FROM proveedores WHERE estado = 'baneado'")
    Integer contarProveedoresBaneados();

    @Query("SELECT COUNT(*) FROM usuarios WHERE estado = 'activo'")
    Integer usuariosActivos();

    @Query("SELECT COUNT(*) FROM usuarios WHERE estado = 'inactivo'")
    Integer usuariosInactivos();

    //dashboard admi zonal
    @Query(value="SELECT COUNT(*) FROM usuarios WHERE idzonas=:idzonas",nativeQuery = true)
    Integer usuariosRegistradosPorZona(@Param("idzonas") Integer idzonas);

    @Query(value="SELECT COUNT(*) FROM usuarios WHERE estado = 'activo' and idzonas=:idzonas",nativeQuery = true)
    Integer usuariosActivosPorZona(@Param("idzonas") Integer idzonas);
    @Query(value = """
            SELECT\s
                agente.idusuarios AS idAgente,
                agente.nombre AS nombreAgente,
                agente.dni AS dniAgente,
                agente.email AS emailAgente,
                COUNT(empleado.idusuarios) AS cantidadUsuariosAsignados
            FROM\s
                usuarios AS jefe
            JOIN\s
                usuarios AS agente ON agente.usuarios_idusuarios = jefe.idusuarios
            LEFT JOIN\s
                usuarios AS empleado ON empleado.usuarios_idusuarios = agente.idusuarios
            WHERE\s
                jefe.idusuarios = :idJefe
            GROUP BY\s
                agente.idusuarios, agente.nombre, agente.dni, agente.email;
            """, nativeQuery = true)
    List<AgenteDTO> findAgentesByJefeId(@Param("idJefe") Integer idJefe);

    /*
    @Query(value = """
                    SELECT 
                        u.nombre,
                        SUM(d.cantidad) AS totalCantidad
                    FROM 
                        ordenes AS o
                    JOIN 
                        detallesorden AS d ON o.idordenes = d.idorden
                    JOIN 
                        usuarios AS u ON o.idusuarios = u.idusuarios
                    GROUP BY 
                        u.nombre;
                    """, nativeQuery = true)
    List<UsuarioCantidadDTO> obtenerTotalCantidadPorUsuario();
    */
    @Query(nativeQuery = true, value = "select * from usuarios where idroles=3 and idzonas=:idzonas and estado='activo'")
    List<Usuario> listarAgentesPorZona(@Param("idzonas") Integer idzonas);

    @Query(nativeQuery = true, value = "select * from dogless.usuarios where idroles=4 and idzonas=:idzonas and estado='activo'")
    List<Usuario> findUsuariosAsignadosAlAgente(Integer idzonas);

    Optional<Usuario> findByNombre(String username);
}

