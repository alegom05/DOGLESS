package com.example.holamundo.entity2;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;

@Entity
@Getter
@Setter
public class Distrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer iddistritos;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "idzonas")
    private Zona zona;
}
