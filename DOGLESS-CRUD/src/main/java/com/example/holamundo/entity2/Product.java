package com.example.holamundo.entity2;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Product {

    @Column(nullable = false)
    @NotBlank
    @Size(max = 40, message = "El nombre no puede tener mas de 40 caracteres")
    private String productname;
    @Positive
    @Digits(integer = 10, fraction = 4)
    private BigDecimal unitprice;
    @Digits(integer = 10, fraction = 0)
    @Max(value = 32767)
    @Min(value = 0)
    private Integer unitsinstock;
    @Digits(integer = 10, fraction = 0)
    @Max(value = 32767)
    @Min(value = 0)
    private Integer unitsonorder;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="productid")
    private int id;
    @ManyToOne
    @JoinColumn(name = "supplierid")
    private Supplier supplier;
    @ManyToOne
    @JoinColumn(name = "categoryid")
    private Category category;
    private String quantityperunit;

    @Column(nullable = false)
    private boolean discontinued;

    //Integer acepta valores nulos
    private Integer reorderlevel;

    public Product(int id, String productname) {
        this.id = id;
        this.productname = productname;
    }

    public Product() {

    }
}
