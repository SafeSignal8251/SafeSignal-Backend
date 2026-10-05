package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZonaRiesgoDTO {
    private Long id_zona;
    private String nombre;
    private String nivel_riesgo;
    private Double puntaje_riesgo;
    private Double latitud;
    private Double longitud;
    private Double radio_metros;
    private LocalDateTime fecha_actualizacion;
    private Boolean estado;
    private Integer umbral_alto_min;
    private Integer umbral_medio_min;
    private Integer umbral_medio_max;
    private Integer umbral_bajo_max;
    private Integer ventana_dias;
    private Double radio_geocerca;
}
