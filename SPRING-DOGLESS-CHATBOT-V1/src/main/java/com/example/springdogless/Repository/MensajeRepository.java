package com.example.springdogless.Repository;

import com.example.springdogless.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {

}
