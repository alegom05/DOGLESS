package com.example.springdogless.Repository;

import com.example.springdogless.entity.Proveedor;
import com.example.springdogless.entity.Reposicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReposicionesRepository extends JpaRepository<Reposicion, Integer> {
    List<Reposicion> findByAprobarIsNull(); // Método para encontrar reposiciones con aprobado nulo
}
