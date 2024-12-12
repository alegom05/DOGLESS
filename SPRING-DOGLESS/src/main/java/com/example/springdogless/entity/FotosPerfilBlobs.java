package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity(name="fotosperfilblobs")
@Getter
@Setter
public class FotosPerfilBlobs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFotosPerfilBlobs;

    @Lob
    @Column(name = "fotoPefil")
    private byte[] fotoPerfil;

    @ManyToOne
    @JoinColumn(name = "usuarios_idusuarios")
    private Usuario usuario;
}
