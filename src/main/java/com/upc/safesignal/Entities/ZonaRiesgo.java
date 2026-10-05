package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "zona_riesgo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ZonaRiesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_zona")
    private Long id_zona;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "nivel_riesgo", length = 20)
    private String nivel_riesgo;

    @Column(name = "puntaje_riesgo")
    private Double puntaje_riesgo;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "radio_metros")
    private Double radio_metros;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fecha_actualizacion;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "umbral_alto_min")
    private Integer umbral_alto_min = 15;

    @Column(name = "umbral_medio_min")
    private Integer umbral_medio_min = 6;

    @Column(name = "umbral_medio_max")
    private Integer umbral_medio_max = 14;

    @Column(name = "umbral_bajo_max")
    private Integer umbral_bajo_max = 5;

    @Column(name = "ventana_dias")
    private Integer ventana_dias = 30;

    @Column(name = "radio_geocerca")
    private Double radio_geocerca;
}