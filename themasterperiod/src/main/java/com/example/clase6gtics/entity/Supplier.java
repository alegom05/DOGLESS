package com.example.clase6gtics.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplierid")
    private int id;
    @Column(nullable = false)
    private String companyname;
    private String contactname;
    private String address;
    private String city;
    private String region;
    private String postalcode;
    private String country;
    private String phone;
    private String fax;
    private String homepage;

}

