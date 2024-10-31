package com.example.springdogless.Repository;


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
    @Query("SELECT p.nombre AS productos, u.nombre AS usuarios, SUM(d.cantidad) AS cantidadTotal " +
            "FROM detallesorden d " +
            "JOIN d.producto p " +
            "JOIN d.orden o " +
            "JOIN o.usuario u " +
            "GROUP BY p.nombre, u.nombre")
    List<Object[]> obtenerCantidadPorProductoYUsuario();

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

    @Query("SELECT o FROM ordenes o WHERE MONTH(o.fecha) = :mes")
    List<Orden> findOrdenesByMes(@Param("mes") int mes);

}

