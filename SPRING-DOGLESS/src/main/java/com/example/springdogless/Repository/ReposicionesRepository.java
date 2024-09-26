package com.example.springdogless.Repository;

import com.example.springdogless.entity.Proveedor;
import com.example.springdogless.entity.Reposicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReposicionesRepository extends JpaRepository<Reposicion, Integer> {

}
