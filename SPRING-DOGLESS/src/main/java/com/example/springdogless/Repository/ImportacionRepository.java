package com.example.springdogless.Repository;

import com.example.springdogless.entity.Importacion;
import com.example.springdogless.entity.Orden;
import com.example.springdogless.entity.Reposicion;
import com.example.springdogless.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImportacionRepository extends JpaRepository<Importacion, Integer> {
    Optional<Importacion> findById(Integer idimportaciones);
    List<Importacion> findByBorrado(Integer borrado);
}
