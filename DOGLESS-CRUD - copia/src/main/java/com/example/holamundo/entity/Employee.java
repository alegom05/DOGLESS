package com.example.holamundo.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.sql.Date;

@Entity (name="employees")
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EmployeeID", nullable = false)
    private int id;

    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false)
    private String firstname;

    private String title;
    private String titleOfCourtesy;
    private Date birthDate;
    private Date hireDate;
    private String address;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private String homePhone;
    private String extension;

    @Lob
    private byte[] photo;

    @Column(nullable = false)
    private String notes;

    @OneToOne
    @JoinColumn (name="reportsto")
    private Employee manager;

    private String photoPath;
    private float salary; 

    /*
        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EmployeeID")
    private int id;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String firstName;
    private String title;
    private String titleOfCourtesy;
    private Date birthDate;
    private Date hireDate;
    private String address;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private String homePhone;
    private String extension;
    @Lob
    private byte[] photo;
    @Column(nullable = false)
    private String notes;
    @OneToOne
    @JoinColumn(name = "reportsTo")
    private Employee manager;
    private String photoPath;
    private float salary;
    */


}
