package com.example.springdogless.Repository;

import com.example.springdogless.entity.Detalleorden;
import com.example.springdogless.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface DetallesordenRepository extends JpaRepository<Detalleorden, Integer> {
}
