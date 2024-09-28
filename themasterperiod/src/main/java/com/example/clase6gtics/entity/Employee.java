package com.example.clase6gtics.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "employees")
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employeeid")
    private int id;
    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false)
    private String firstname;
    private String title;
    private String titleofcourtesy;
    private Date birthdate;
    private Date hiredate;
    private String address;
    private String city;
    private String region;
    private String postalcode;
    private String country;
    private String homephone;
    private String extension;
    @Lob
    private byte[] photo;
    @Column(nullable = false)
    private String notes;
    @OneToOne
    @JoinColumn(name = "reportsto")
    private Employee manager;
    private String photopath;
    private float salary;

}
