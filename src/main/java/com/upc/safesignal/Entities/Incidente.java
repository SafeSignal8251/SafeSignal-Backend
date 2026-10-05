package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidente")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incidente")
    private Long id_incidente;

    @Column(name = "tipo_incidente", length = 100)
    private String tipo_incidente;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "fecha_hora")
    private LocalDateTime fecha_hora;

    @Column(name = "nivel_riesgo", length = 20)
    private String nivel_riesgo;

    @Column(name = "estado", length = 30)
    private String estado;

    @Column(name = "direccion", length = 150)
    private String direccion;

    @Column(name = "distrito", length = 50)
    private String distrito;

    @ManyToOne
    @JoinColumn(name = "id_zona")
    private ZonaRiesgo zona;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}