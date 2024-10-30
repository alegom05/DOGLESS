package com.example.springdogless.Repository;

import com.example.springdogless.entity.ProductoZona;
import com.example.springdogless.entity.Stockproducto2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockProductoRepository extends JpaRepository<ProductoZona, Stockproducto2> {
    Optional<ProductoZona> findByStockproductoIdproductosAndStockproductoIdzonas(Integer idproductos, Integer idzonas);

}
