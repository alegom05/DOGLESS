package com.example.holamundo.entity;

import com.example.holamundo.repository.ShipperRepository;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.engine.transaction.jta.platform.internal.SunOneJtaPlatform;
import org.hibernate.event.spi.SaveOrUpdateEventListener;

import java.math.BigDecimal;

@Getter
@Setter
@Entity (name="products")
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
    @Column(name="ProductID")
    private int id;
    @ManyToOne
    @JoinColumn(name = "SupplierID")
    private Supplier supplier;
    @ManyToOne
    @JoinColumn(name = "CategoryID")
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
