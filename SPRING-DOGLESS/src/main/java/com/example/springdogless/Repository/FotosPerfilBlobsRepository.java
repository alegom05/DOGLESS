package com.example.springdogless.Repository;

import com.example.springdogless.entity.FotosPerfilBlobs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FotosPerfilBlobsRepository extends JpaRepository<FotosPerfilBlobs, Integer> {
    Optional<FotosPerfilBlobs> findByUsuario_Id(Integer id);
}
