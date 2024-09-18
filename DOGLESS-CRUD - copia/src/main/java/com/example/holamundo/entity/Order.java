package com.example.holamundo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity (name="orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID", nullable = false)
    private Integer orderID;

    private String customerid;
    private int employeeid;
    private Date orderdate;
    private Date requireddate;
    private Date shippeddate;

    @ManyToOne
    @JoinColumn(name = "ShipVia")
    private Shipper shipvia;

    private Double freight;
    private String shipname;
    private String shipaddress;
    private String shipcity;
    private String shipregion;
    private String shippostalcode;
    private String shipcountry;



    // Other fields and getters and setters
}
