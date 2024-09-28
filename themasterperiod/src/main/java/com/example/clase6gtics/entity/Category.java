package com.example.clase6gtics.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;


@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryid")
    private int id;
    @Column(nullable = false)
    private String categoryname;
    private String description;
    @Lob
    private byte[] picture;
}
