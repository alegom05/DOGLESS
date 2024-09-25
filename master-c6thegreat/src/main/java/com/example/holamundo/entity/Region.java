package com.example.holamundo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity (name="region")
public class Region {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name= "regionid", nullable=false)
    private Integer id;

    @Column(name= "regiondescription", nullable =false)
    private String name;



}
