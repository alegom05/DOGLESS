package com.example.springdogless.Repository;


import com.example.springdogless.entity.Proveedor;
import com.example.springdogless.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer>{

    /*
    public List<Proveedor> findByCategory(Proveedor proveedor);
    */
    @Query(nativeQuery = true, value = "select * from proveedores where borrado = 1")
    List<Proveedor> findByProveedoresActivos();

    Optional<Proveedor> findByNombre(String productName);

    Optional<Proveedor> findById(Integer id);

    Optional<Proveedor> findByDni(String dni);

    //Dashboard Admin
    @Query("SELECT p.id, COUNT(p.id) AS cantidad " +
            "FROM productos p " +
            "GROUP BY p.id " +
            "ORDER BY cantidad DESC")
    List<Object[]> findTop5Proveedores();


}

