package com.example.holamundo.entity;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.dialect.function.ListaggStringAggEmulation;

@Entity (name="suppliers")
@Getter
@Setter
public class Supplier {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name= "supplierid", nullable=false)
    private Integer id;
    private String companyname;
    private String city;
    private String region;
    private String phone;

}
