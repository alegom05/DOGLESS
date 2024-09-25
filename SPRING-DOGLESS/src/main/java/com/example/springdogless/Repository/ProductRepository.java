package com.example.springdogless.Repository;


import com.example.springdogless.entity.Producto;
import com.example.springdogless.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Producto, Integer>{
    List<Producto> findByBorrado(int borrado);

    List<Optional> findByEstado(String estado);

}

