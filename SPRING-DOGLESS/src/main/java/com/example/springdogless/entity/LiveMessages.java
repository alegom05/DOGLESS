package com.example.springdogless.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "livemessages")
public class LiveMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idlivemessages", nullable = false)
    private Integer idlivemessages;

    @NotNull
    @Lob
    @Column(name = "contenido", nullable = false)
    private String contenido;

    @Size(max = 45)
    @NotNull
    @Column(name = "sala", nullable = false, length = 45)
    private String sala;
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idusuarios", nullable = false)
    @JsonIgnore
    private Usuario idusuarios;
    @NotNull
    @Column(name = "fechaenvio", nullable = false)
    private LocalDateTime fechaenvio;

}