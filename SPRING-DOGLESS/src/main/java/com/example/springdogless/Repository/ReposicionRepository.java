package com.example.springdogless.Repository;

import com.example.springdogless.entity.Reposicion;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReposicionRepository extends JpaRepository<Reposicion, Integer> {
    List<Reposicion> findByAprobarIsNull(); // Método para encontrar reposiciones con aprobado nulo
    List<Reposicion> findByAprobar(String aprobado);

    Optional<Reposicion> findById(Integer id);

    List<Reposicion> findByBorrado(Integer borrado);


}
