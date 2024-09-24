package com.example.holamundo.repository2;

import com.example.holamundo.entity2.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ShipperRepository extends JpaRepository<Shipper, Integer> {

    List<Shipper> findByCompanyname (String nombre);

    //Notación @Query
    /*@Query(value = "select * from shippers where CompanyName = ?", nativeQuery = true)
    List<Shipper> buscarTransPorCompName(String nombre);*/

    //Segunda notación, búsqueda parcial
    @Query(nativeQuery = true, value = "select * from shippers where CompanyName like %?1%")
    List<Shipper> buscarPorNombreParcial(String companyName);
    @Transactional
    @Modifying
    @Query (nativeQuery = true, value = "update shippers set CompanyName = ?1 where ShipperID = ?2")
    void actualizarNombreCompania(String companyName, int shipperId);

}