package com.example.springdogless.Repository;


import com.example.springdogless.entity.Solicitud;
import com.example.springdogless.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer>{
    @Query(nativeQuery = true, value = "select * from solicitudes where veredicto IS NULL")
    List<Solicitud> findSolicitudes();

}