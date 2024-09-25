package com.example.holamundo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity (name="shippers")
public class Shipper {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name= "shipperid", nullable=false)
    private Integer id;

    @Column(name= "companyname", nullable =false)
    private String companyname;

    @Column(name= "phone")
    private String phone;



}
