package com.example.springdogless.Repository;

import com.example.springdogless.entity.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZonalRepository extends JpaRepository<Zona, Integer> {
}
