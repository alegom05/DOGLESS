package com.example.springdogless.Repository;

import com.example.springdogless.entity.Preguntasproducto;
import com.example.springdogless.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreguntasProductoRepository extends JpaRepository<Preguntasproducto, Integer> {
}
