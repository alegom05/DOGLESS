package com.example.springdogless.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity (name="usuarios")
@Getter
@Setter
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="idusuarios")
    private Integer id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private String direccion;

    private String pwd;
    private Integer borrado;
    private Date fechabaneo;
    private String motivobaneo;


    @ManyToOne
    @JoinColumn(name = "idroles")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "iddistritos")
    private Distrito distrito;

    private Integer idadminzonales;

    private String estado;
    private String ruc;
    private String codigoaduana;
    private String razonsocial;
    private String codigojurisdiccion;

    @ManyToOne
    @JoinColumn(name = "usuarios_idusuarios")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idzonas")
    private Zona zona;
    private String fechanacimiento;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Orden> orden;

    @Lob
    @Column(name = "fotoperfil", columnDefinition = "BLOB")
    private byte[] fotoperfil;  // Cambiado a byte[] para el campo BLOB

}
