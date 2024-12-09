package com.example.springdogless.Repository;


import com.example.springdogless.DTO.ProductoDTO;
import com.example.springdogless.entity.Producto;
import com.example.springdogless.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Producto, Integer>{
    @Query(nativeQuery = true, value = "select * from productos where borrado like %?1%")
    Optional<Producto> findByproductosinBorrar(Integer borrado);
    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, s.idzonas, z.nombre AS nombreZona " +
            "FROM dogless.productos p " +
            " LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos" +
            " LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "WHERE s.borrado = 1", nativeQuery = true)
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
    /*backup
    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona " +
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "WHERE s.idzonas = ?1 " +
            "AND (?2 IS NULL OR p.precio BETWEEN ?2 AND ?3) " + // Filtrar por precio si se proporciona
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1 " +
                    "AND (?2 IS NULL OR p.precio BETWEEN ?2 AND ?3)", // Filtrar en el conteo por precio si se proporciona
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaAndPrecio(
            Integer idzona,
            Double precioMin,
            Double precioMax,
            Pageable pageable);
    */


    @Query(value = "SELECT " +
            "p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona, " +
            "ROUND(COALESCE(avgSatisfaccion.promedio_satisfaccion, 0)) AS promedioSatisfaccion " + // Columna de satisfacción promedio
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "LEFT JOIN ( " +
            "    SELECT r.idproductos, AVG(r.satisfaccion) AS promedio_satisfaccion " +
            "    FROM dogless.resenas r " +
            "    JOIN dogless.usuarios u ON r.usuarioid = u.idusuarios " +
            "    GROUP BY r.idproductos " +
            ") avgSatisfaccion ON p.idproductos = avgSatisfaccion.idproductos " +
            "WHERE s.idzonas = ?1 " + // Parámetro para el ID de zona
            "AND (?2 IS NULL OR p.precio BETWEEN ?2 AND ?3) " + // Parámetros para el rango de precios
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",

            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1 " +
                    "AND (?2 IS NULL OR p.precio BETWEEN ?2 AND ?3)", // Conteo de productos filtrados
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaAndPrecio(
            Integer idzona,
            Double precioMin,
            Double precioMax,
            Pageable pageable);




    @Query(value = "SELECT COUNT(p.idproductos) FROM dogless.productos p " +
            "JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "WHERE s.idzonas = ?1 AND p.categoria = ?2", nativeQuery = true)
    Integer countProductosByZonaAndCategoria(Integer idzona, String categoria);
    /* backup
    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona " +
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "WHERE s.idzonas = ?1 " +
            "AND p.categoria = ?2 " +
            "AND (?3 IS NULL OR p.precio BETWEEN ?3 AND ?4) " + // Filtro opcional por precio
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1 " +
                    "AND p.categoria = ?2 " +
                    "AND (?3 IS NULL OR p.precio BETWEEN ?3 AND ?4)", // Filtro en el conteo por precio
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaAndCategoriaAndPrecio(
            Integer idzona,
            String categoria,
            Double minPrecio,  // Parámetro para el precio mínimo
            Double maxPrecio,  // Parámetro para el precio máximo
            Pageable pageable);
    */



    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona, " +
            "ROUND(COALESCE(avgSatisfaccion.promedio_satisfaccion, 0)) AS promedioSatisfaccion " + // Agrega la columna de satisfacción promedio
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "LEFT JOIN ( " +
            "    SELECT r.idproductos, " +
            "           ROUND(COALESCE(AVG(r.satisfaccion), 0)) AS promedio_satisfaccion " +
            "    FROM dogless.resenas r " +
            "    JOIN dogless.usuarios u ON r.usuarioid = u.idusuarios " +
            "    GROUP BY r.idproductos " +
            ") avgSatisfaccion ON p.idproductos = avgSatisfaccion.idproductos " +
            "WHERE s.idzonas = ?1 " +
            "AND p.categoria = ?2 " +
            "AND (?3 IS NULL OR p.precio BETWEEN ?3 AND ?4) " + // Filtro opcional por precio
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1 " +
                    "AND p.categoria = ?2 " +
                    "AND (?3 IS NULL OR p.precio BETWEEN ?3 AND ?4)", // Filtro en el conteo por precio
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaAndCategoriaAndPrecio(
            Integer idzona,
            String categoria,
            Double minPrecio,  // Parámetro para el precio mínimo
            Double maxPrecio,  // Parámetro para el precio máximo
            Pageable pageable);






    // Método para contar productos por zona
    @Query(value = "SELECT COUNT(*) FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "WHERE s.idzonas = ?1",
            nativeQuery = true)
    Integer countProductosByZona(Integer idzona);

    @Query(value = "SELECT DISTINCT p.categoria FROM dogless.productos p JOIN dogless.stockproductos s ON p.idproductos = s.idproductos WHERE s.idzonas = ?1", nativeQuery = true)
    List<String> findAllCategorias(Integer idzona);

    // Método para obtener productos por zona ordenados por precio de mayor a menor



    //Para la lista de productos en base a las ordenes del usuario
    @Query(value = "SELECT p.* FROM usuarios u JOIN ordenes o ON u.idusuarios = o.idusuarios JOIN detallesorden ohp ON o.idordenes = ohp.idorden JOIN productos p ON ohp.idproducto = p.idproductos WHERE u.idusuarios = ? AND o.estado = 'CREADO'", nativeQuery = true)
    List<Producto> findProductosByUsuarioId(Integer id);



    /* backup
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
    */



    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona, " +
            "ROUND(COALESCE(avgSatisfaccion.promedio_satisfaccion, 0)) AS promedioSatisfaccion " + // Agrega la columna de satisfacción promedio
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "LEFT JOIN ( " +
            "    SELECT r.idproductos, " +
            "           ROUND(COALESCE(AVG(r.satisfaccion), 0)) AS promedio_satisfaccion " +
            "    FROM dogless.resenas r " +
            "    JOIN dogless.usuarios u ON r.usuarioid = u.idusuarios " +
            "    GROUP BY r.idproductos " +
            ") avgSatisfaccion ON p.idproductos = avgSatisfaccion.idproductos " +
            "WHERE s.idzonas = ?1 " +
            "AND p.categoria = ?2 " + // Filtrar por categoría
            "ORDER BY p.precio DESC " + // Ordenar por precio de mayor a menor
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1 " +
                    "AND p.categoria = ?2", // Filtrar en el conteo por categoría
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaAndCategoriaOrderByPrecioDescByCategoria(
            Integer idzona,
            String categoria,
            Pageable pageable);









    /*
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
    */




    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona, " +
            "ROUND(COALESCE(avgSatisfaccion.promedio_satisfaccion, 0)) AS promedioSatisfaccion " + // Agrega la columna de satisfacción promedio
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "LEFT JOIN ( " +
            "    SELECT r.idproductos, " +
            "           ROUND(COALESCE(AVG(r.satisfaccion), 0)) AS promedio_satisfaccion " +
            "    FROM dogless.resenas r " +
            "    JOIN dogless.usuarios u ON r.usuarioid = u.idusuarios " +
            "    GROUP BY r.idproductos " +
            ") avgSatisfaccion ON p.idproductos = avgSatisfaccion.idproductos " +
            "WHERE s.idzonas = ?1 " +
            "AND p.categoria = ?2 " + // Filtrar por categoría
            "ORDER BY p.precio ASC " + // Ordenar por precio de menor a mayor
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1 " +
                    "AND p.categoria = ?2", // Filtrar en el conteo por categoría
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaAndCategoriaOrderByPrecioAscByCategoria(
            Integer idzona,
            String categoria,
            Pageable pageable);






/*
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

  */


    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona, " +
            "ROUND(COALESCE(avgSatisfaccion.promedio_satisfaccion, 0)) AS promedioSatisfaccion " + // Agrega la columna de satisfacción promedio
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "LEFT JOIN ( " +
            "    SELECT r.idproductos, " +
            "           ROUND(COALESCE(AVG(r.satisfaccion), 0)) AS promedio_satisfaccion " +
            "    FROM dogless.resenas r " +
            "    JOIN dogless.usuarios u ON r.usuarioid = u.idusuarios " +
            "    GROUP BY r.idproductos " +
            ") avgSatisfaccion ON p.idproductos = avgSatisfaccion.idproductos " +
            "WHERE s.idzonas = ?1 " + // Filtrar solo por zona
            "ORDER BY p.precio ASC " + // Ordenar por precio de menor a mayor
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1", // Filtrar en el conteo solo por zona
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaOrderByPrecioAsc(
            Integer idzona,
            Pageable pageable);



/*
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


    */






    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona, " +
            "ROUND(COALESCE(avgSatisfaccion.promedio_satisfaccion, 0)) AS promedioSatisfaccion " + // Agrega la columna de satisfacción promedio
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "LEFT JOIN ( " +
            "    SELECT r.idproductos, " +
            "           ROUND(COALESCE(AVG(r.satisfaccion), 0)) AS promedio_satisfaccion " +
            "    FROM dogless.resenas r " +
            "    JOIN dogless.usuarios u ON r.usuarioid = u.idusuarios " +
            "    GROUP BY r.idproductos " +
            ") avgSatisfaccion ON p.idproductos = avgSatisfaccion.idproductos " +
            "WHERE s.idzonas = ?1 " + // Filtrar solo por zona
            "ORDER BY p.precio DESC " + // Ordenar por precio de mayor a menor
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1", // Filtrar en el conteo solo por zona
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaOrderByPrecioDesc(
            Integer idzona,
            Pageable pageable);



    @Query(value = "SELECT " +
            "p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona, " +
            "ROUND(COALESCE(avgSatisfaccion.promedio_satisfaccion, 0)) AS promedioSatisfaccion " + // Columna de satisfacción promedio
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "LEFT JOIN ( " +
            "    SELECT r.idproductos, AVG(r.satisfaccion) AS promedio_satisfaccion " +
            "    FROM dogless.resenas r " +
            "    JOIN dogless.usuarios u ON r.usuarioid = u.idusuarios " +
            "    GROUP BY r.idproductos " +
            ") avgSatisfaccion ON p.idproductos = avgSatisfaccion.idproductos " +
            "WHERE s.idzonas = ?1 " + // Parámetro para el ID de zona
            "ORDER BY promedioSatisfaccion DESC " + // Ordenar por satisfacción promedio descendente
            "LIMIT 3", // Obtener solo los 3 primeros
            nativeQuery = true)
    List<ProductoDTO> findTop3ProductosByZona(Integer idzona);






    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona, " +
            "ROUND(COALESCE(avgSatisfaccion.promedio_satisfaccion, 0)) AS promedioSatisfaccion " + // Agrega la columna de satisfacción promedio
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "LEFT JOIN ( " +
            "    SELECT r.idproductos, " +
            "           ROUND(COALESCE(AVG(r.satisfaccion), 0)) AS promedio_satisfaccion " +
            "    FROM dogless.resenas r " +
            "    JOIN dogless.usuarios u ON r.usuarioid = u.idusuarios " +
            "    GROUP BY r.idproductos " +
            ") avgSatisfaccion ON p.idproductos = avgSatisfaccion.idproductos " +
            "WHERE s.idzonas = ?1 " +
            "AND p.categoria = ?2 " + // Filtrar por categoría
            "ORDER BY promedioSatisfaccion DESC " + // Ordenar por satisfacción de mayor a menor
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1 " +
                    "AND p.categoria = ?2", // Filtrar en el conteo por categoría
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaAndCategoriaOrderBySatisfaccionDesc(
            Integer idzona,
            String categoria,
            Pageable pageable);





    @Query(value = "SELECT p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "s.cantidad, z.nombre AS nombreZona, " +
            "ROUND(COALESCE(avgSatisfaccion.promedio_satisfaccion, 0)) AS promedioSatisfaccion " + // Agrega la columna de satisfacción promedio
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "LEFT JOIN ( " +
            "    SELECT r.idproductos, " +
            "           ROUND(COALESCE(AVG(r.satisfaccion), 0)) AS promedio_satisfaccion " +
            "    FROM dogless.resenas r " +
            "    JOIN dogless.usuarios u ON r.usuarioid = u.idusuarios " +
            "    GROUP BY r.idproductos " +
            ") avgSatisfaccion ON p.idproductos = avgSatisfaccion.idproductos " +
            "WHERE s.idzonas = ?1 " + // Filtrar solo por zona
            "ORDER BY promedioSatisfaccion DESC " + // Ordenar por satisfacción de mayor a menor
            "LIMIT ?#{#pageable.pageSize} OFFSET ?#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM dogless.productos p " +
                    "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
                    "WHERE s.idzonas = ?1", // Filtrar en el conteo solo por zona
            nativeQuery = true)
    Page<ProductoDTO> findProductosByZonaOrderBySatisfaccionDesc(
            Integer idzona,
            Pageable pageable);


    @Query(value =
            "SELECT " +
                    "   prov.idproveedores, " +
                    "   prov.nombre AS proveedorNombre, " +
                    "   prov.apellido AS proveedorApellido, " +
                    "   SUM(CASE WHEN r.aprobar = 'aprobado' THEN r.cantidad ELSE 0 END) AS reposicionesAprobadas, " +
                    "   ROUND(AVG(avgSatisfaccion.promedio_satisfaccion), 4) AS promedioCalificacionProveedor " +
                    "FROM " +
                    "   dogless.proveedores prov " +
                    "JOIN " +
                    "   dogless.productos p ON p.idproveedores = prov.idproveedores " +
                    "LEFT JOIN " +
                    "   dogless.reposicion r ON r.idproductos = p.idproductos " +
                    "LEFT JOIN ( " +
                    "   SELECT " +
                    "       r.idproductos, " +
                    "       COALESCE(AVG(r.satisfaccion), 0) AS promedio_satisfaccion " +
                    "   FROM " +
                    "       dogless.resenas r " +
                    "   GROUP BY " +
                    "       r.idproductos " +
                    ") avgSatisfaccion ON p.idproductos = avgSatisfaccion.idproductos " +
                    "GROUP BY " +
                    "   prov.idproveedores, prov.nombre, prov.apellido " +
                    "ORDER BY " +
                    "   reposicionesAprobadas DESC " +
                    "LIMIT 5",
            nativeQuery = true)
    List<ProductoDTO> obtenerTop5ProveedoresConReposicionesYCalificaciones();


    @Query(value =
            "SELECT " +
                    "   p.idproductos, " +
                    "   p.nombre AS nombre, " +
                    "   SUM(CASE WHEN r.aprobar = 'aprobado' THEN r.cantidad ELSE 0 END) AS cantidadAprobada " +
                    "FROM " +
                    "   dogless.productos p " +
                    "JOIN " +
                    "   dogless.reposicion r ON r.idproductos = p.idproductos " +
                    "GROUP BY " +
                    "   p.idproductos, p.nombre " +
                    "ORDER BY " +
                    "   cantidadAprobada DESC " +
                    "LIMIT 10",
            nativeQuery = true)
    List<ProductoDTO> obtenerTop10ProductosMasImportados();

    @Query(value =
            "SELECT " +
                    "   prov.idproveedores, " +
                    "   prov.nombre AS proveedorNombre, " +
                    "   prov.apellido AS proveedorApellido, " +
                    "   ROUND(AVG(avgSatisfaccion.promedio_satisfaccion), 4) AS promedioCalificacionProveedor, " +
                    "   COUNT(CASE WHEN r.satisfaccion <= 3 THEN 1 END) AS comentariosNegativosProveedor " +
                    "FROM " +
                    "   dogless.proveedores prov " +
                    "JOIN " +
                    "   dogless.productos p ON p.idproveedores = prov.idproveedores " +
                    "LEFT JOIN " +
                    "   dogless.resenas r ON r.idproductos = p.idproductos " +
                    "LEFT JOIN ( " +
                    "   SELECT " +
                    "       r.idproductos, " +
                    "       COALESCE(AVG(r.satisfaccion), 0) AS promedio_satisfaccion " +
                    "   FROM " +
                    "       dogless.resenas r " +
                    "   GROUP BY " +
                    "       r.idproductos " +
                    ") avgSatisfaccion ON p.idproductos = avgSatisfaccion.idproductos " +
                    "GROUP BY " +
                    "   prov.idproveedores, prov.nombre, prov.apellido " +
                    "ORDER BY " +
                    "   promedioCalificacionProveedor ASC " +
                    "LIMIT 5",
            nativeQuery = true)
    List<ProductoDTO> obtenerTop3ProveedoresConPeoresValoraciones();




    //Solo 1 producto
    @Query(value = "SELECT " +
            "p.idproductos, p.nombre, p.descripcion, p.categoria, p.precio, p.costoenvio, " +
            "p.idproveedores, prov.nombre AS proveedorNombre, prov.apellido AS proveedorApellido, " +
            "p.modelos, p.colores, p.aprobado, p.borrado, p.estado, " +
            "z.nombre AS nombreZona, " +
            "ROUND(COALESCE(avgSatisfaccion.promedio_satisfaccion, 0)) AS promedioSatisfaccion " + // Columna de satisfacción promedio
            "FROM dogless.productos p " +
            "LEFT JOIN dogless.stockproductos s ON p.idproductos = s.idproductos " +
            "LEFT JOIN dogless.zonas z ON s.idzonas = z.idzonas " +
            "JOIN dogless.proveedores prov ON p.idproveedores = prov.idproveedores " +
            "LEFT JOIN ( " +
            "    SELECT r.idproductos, AVG(r.satisfaccion) AS promedio_satisfaccion " +
            "    FROM dogless.resenas r " +
            "    JOIN dogless.usuarios u ON r.usuarioid = u.idusuarios " +
            "    GROUP BY r.idproductos " +
            ") avgSatisfaccion ON p.idproductos = avgSatisfaccion.idproductos " +
            "WHERE p.idproductos = ?1 " + // Espacio añadido aquí
            "AND s.idzonas = ?2", // Espacio añadido aquí
            nativeQuery = true)
    ProductoDTO findProductoByIdByZonaCompleto(Integer idProducto, Integer idZona);

    //Querys del Dashboard

    /*
    @Query("SELECT new map(p.nombre as nombre, COUNT(p) as cantidad) " +
            "FROM productos p " +
            "GROUP BY p.nombre " +
            "ORDER BY cantidad DESC " +
            "LIMIT 10")
    List<Map<String, Object>> obtenerTop10Productos();
    */

    @Query("SELECT COUNT(u) FROM usuarios u WHERE u.estado = :estado")
    int contarPorEstado(String estado);

    // de agentes D:

    @Query(value = "SELECT " +
            "    p.idproductos, " +
            "    p.nombre AS nombre, " +
            "    SUM(d.cantidad) AS total_vendido " +
            "FROM " +
            "    detallesorden d " +
            "JOIN " +
            "    productos p ON d.idproducto = p.idproductos " +
            "WHERE " +
            "    d.borrado = 1 " +
            "GROUP BY " +
            "    p.idproductos " +
            "ORDER BY " +
            "    total_vendido DESC " +
            "LIMIT 10", nativeQuery = true)
    List<ProductoDTO> contarTotalVendidosPorProducto();

    Producto findByNombre(String mensaje);

    Optional<Producto> findById(Integer id);

}

