package com.example.springdogless.Repository;


import com.example.springdogless.DTO.ProductoDTO;
import com.example.springdogless.entity.Rol;
import com.example.springdogless.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}

