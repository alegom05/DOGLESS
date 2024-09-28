package com.example.clase6gtics.entity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "shippers")
@Getter
@Setter
public class Shipper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipperid")
    private int shipperId;
    @Column(name = "companyname", nullable = false)
    private String companyName;
    private String phone;


}
