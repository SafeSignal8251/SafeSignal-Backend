package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ruta")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Long id_ruta;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "origen", length = 100)
    private String origen;

    @Column(name = "destino", length = 100)
    private String destino;

    @Column(name = "origen_latitud")
    private Double origen_latitud;

    @Column(name = "origen_longitud")
    private Double origen_longitud;

    @Column(name = "destino_latitud")
    private Double destino_latitud;

    @Column(name = "destino_longitud")
    private Double destino_longitud;

    @Column(name = "distancia")
    private Double distancia;

    @Column(name = "tiempo_estimado")
    private Integer tiempo_estimado;

    @Column(name = "tipo_ruta", length = 50)
    private String tipo_ruta;

    @Column(name = "fecha_creacion")
    private LocalDateTime fecha_creacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}