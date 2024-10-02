package com.example.springdogless.Repository;


import com.example.springdogless.DTO.ProductoDTO;
import com.example.springdogless.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Producto, Integer>{
    @Query(nativeQuery = true, value = "select * from productos where borrado like %?1%")
    Optional<Producto> findByproductosinBorrar(Integer borrado);
    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona " +
            "FROM dogless.productos p " +
            " LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos" +
            " LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores", nativeQuery = true)
    List<ProductoDTO> ProductosCompleto();
    List<Producto> findByBorrado(Integer num);

}

