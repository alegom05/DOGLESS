package com.example.springdogless.Repository;
import com.example.springdogless.entity.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ReclamoRepository extends JpaRepository<Reclamo, Integer> {

}
