package com.example.springdogless.Repository;

import com.example.springdogless.DTO.ProductoDTO;
import com.example.springdogless.entity.Distrito;
import com.example.springdogless.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Integer> {

    List<Orden> findByBorrado(Integer borrado);
}
