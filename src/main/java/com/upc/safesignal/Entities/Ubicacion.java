package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ubicacion")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Long id_ubicacion;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "fecha_hora")
    private LocalDateTime fecha_hora;

    @Column(name = "precision_metros")
    private Double precision_metros;

    @ManyToOne
    @JoinColumn(name = "id_seguimiento")
    private Seguimiento seguimiento;
}