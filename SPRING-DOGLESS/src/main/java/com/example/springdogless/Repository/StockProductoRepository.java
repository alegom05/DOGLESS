package com.example.springdogless.Repository;

import com.example.springdogless.DTO.*;
import com.example.springdogless.entity.ProductoZona;
import com.example.springdogless.entity.Stockproducto2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockProductoRepository extends JpaRepository<ProductoZona, Stockproducto2> {
    Optional<ProductoZona> findByStockproductoIdproductosAndStockproductoIdzonas(Integer idproductos, Integer idzonas);

    @Query(value="SELECT p.nombre, s.cantidad FROM productos p\n" +
            "JOIN stockproductos s ON p.idproductos = s.idproductos\n" +
            "ORDER BY s.cantidad ASC",nativeQuery = true)
    List<ProductoStockDTO> findProductosConMenorStock();
}
