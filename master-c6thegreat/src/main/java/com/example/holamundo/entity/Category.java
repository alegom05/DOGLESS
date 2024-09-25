package com.example.holamundo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;

@Entity (name="categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID", nullable = false)
    private int id;

    @Column(nullable = false)
    private String categoryname;

    @Column(name = "Description")
    private String description;

    @Lob
    private byte[] picture;


}
