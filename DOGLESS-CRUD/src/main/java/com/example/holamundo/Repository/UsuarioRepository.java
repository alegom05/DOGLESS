package com.example.holamundo.Repository;


import com.example.holamundo.entity.Usuario;
import com.example.holamundo.entity2.Category;
import com.example.holamundo.entity2.Product;
import com.example.holamundo.entity2.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    @Query(nativeQuery = true, value = "select * from shippers where idroles like %?1%")
    List<Usuario> findByRol(Integer idRol);


}

