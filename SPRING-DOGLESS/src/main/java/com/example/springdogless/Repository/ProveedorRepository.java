package com.example.springdogless.Repository;


import com.example.springdogless.entity.Proveedor;
import com.example.springdogless.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer>{

    /*
    public List<Proveedor> findByCategory(Proveedor proveedor);
    List<Proveedor> findByProveedorName(String productName);
    */

}

