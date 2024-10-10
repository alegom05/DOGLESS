package com.example.springdogless.Repository;


import com.example.springdogless.DTO.ProductoDTO;
import com.example.springdogless.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    /*
    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona " +
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "WHERE s.idzonas = ?1", nativeQuery = true)
    List<ProductoDTO> findProductosByZona(Integer idzona);
    */
    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona " +
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "WHERE s.idzonas = ?1 " +
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1",
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZona(Integer idzona, Pageable pageable);

    @Query(value = "SELECT COUNT(p.idproductos) FROM dogless.productos p " +
            "JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "WHERE s.idzonas = ?1 AND p.categoria = ?2", nativeQuery = true)
    Integer countProductosByZonaAndCategoria(Integer idzona, String categoria);

    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona " +
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "WHERE s.idzonas = ?1 " +
            "AND p.categoria = ?2 " + // Filtrar por categoría
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1 " +
                    "AND p.categoria = ?2", // Filtrar en el conteo por categoría
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaAndCategoria(Integer idzona, String categoria, Pageable pageable);

    // Método para contar productos por zona
    @Query(value = "SELECT COUNT(*) FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "WHERE s.idzonas = ?1",
            nativeQuery = true)
    Integer countProductosByZona(Integer idzona);

    @Query(value = "SELECT DISTINCT p.categoria FROM dogless.productos p JOIN dogless.stockproductos s ON p.idproductos = s.idproductos WHERE s.idzonas = ?1", nativeQuery = true)
    List<String> findAllCategorias(Integer idzona);

    // Método para obtener productos por zona ordenados por precio de mayor a menor

    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona " +
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "WHERE s.idzonas = ?1 " +
            "AND p.categoria = ?2 " + // Filtrar por categoría
            "ORDER BY p.precio DESC " + // Ordenar por precio de mayor a menor
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1 " +
                    "AND p.categoria = ?2", // Filtrar en el conteo por categoría
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaAndCategoriaOrderByPrecioDescByCategoria(Integer idzona, String categoria, Pageable pageable);

    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona " +
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "WHERE s.idzonas = ?1 " +
            "AND p.categoria = ?2 " + // Filtrar por categoría
            "ORDER BY p.precio ASC " + // Ordenar por precio de menor a mayor
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1 " +
                    "AND p.categoria = ?2", // Filtrar en el conteo por categoría
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaAndCategoriaOrderByPrecioAscByCategoria(Integer idzona, String categoria, Pageable pageable);

    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona " +
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "WHERE s.idzonas = ?1 " + // Filtrar solo por zona
            "ORDER BY p.precio ASC " + // Ordenar por precio de menor a mayor
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1", // Filtrar en el conteo solo por zona
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaOrderByPrecioAsc(Integer idzona, Pageable pageable);

    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona " +
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "WHERE s.idzonas = ?1 " + // Filtrar solo por zona
            "ORDER BY p.precio DESC " + // Ordenar por precio de mayor a menor
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1", // Filtrar en el conteo solo por zona
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaOrderByPrecioDesc(Integer idzona, Pageable pageable);
}

