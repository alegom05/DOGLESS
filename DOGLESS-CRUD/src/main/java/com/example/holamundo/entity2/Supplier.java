package com.example.holamundo.entity2;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
