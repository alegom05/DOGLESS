package com.example.clase6gtics.entity;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productid")
    private int id;

    @Column(nullable = false)
    @Size(max = 40,message = "Solo se soportan 40 caractéres")
    @NotBlank
    private String productname;

    @ManyToOne
    @JoinColumn(name = "supplierid")
    private Supplier supplier;
    @ManyToOne
    @JoinColumn(name = "categoryid")
    private Category category;
    private String quantityperunit;
    
    @Digits(integer = 10, fraction = 4)
    @Positive
    private BigDecimal unitprice;

    @Digits(integer = 5,fraction = 0)
    @Min(value = 0)
    @Max(value = 32767,message = "el valor máximo es 32767")
    private int unitsinstock;

    @Digits(integer = 5,fraction = 0)
    @Min(value = 0)
    @Max(value = 32767)
    private Integer unitsonorder;

    private Integer reorderlevel;
    @Column(nullable = false)
    private boolean discontinued;

}
