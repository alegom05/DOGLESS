package com.example.springdogless.Repository;


import com.example.springdogless.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Producto, Integer>{
    @Query(nativeQuery = true, value = "select * from productos where borrado like %?1%")
    Optional<Producto> findByproductosinBorrar(Integer borrado);

    List<Producto> findByBorrado(Integer num);

}

