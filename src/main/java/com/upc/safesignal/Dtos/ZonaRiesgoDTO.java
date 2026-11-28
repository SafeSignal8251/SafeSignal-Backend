package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZonaRiesgoDTO {
    @Schema(description = "Identificador de la zona de riesgo", example = "1")
    private Long id_zona;
    @Schema(description = "Nombre de la zona", example = "San Martín de Porres")
    private String nombre;
    @Schema(description = "Nivel de riesgo: BAJO, MEDIO o ALTO", example = "MEDIO")
    private String nivel_riesgo;
    @Schema(description = "Puntaje de riesgo calculado de la zona", example = "7.5")
    private Double puntaje_riesgo;
    @Schema(description = "Latitud en grados decimales", example = "-12.0464")
    private Double latitud;
    @Schema(description = "Longitud en grados decimales", example = "-77.0428")
    private Double longitud;
    @Schema(description = "Radio en metros", example = "300")
    private Double radio_metros;
    @Schema(description = "Fecha de la última actualización. La asigna el servidor.", example = "2026-11-27T22:00:00")
    private LocalDateTime fecha_actualizacion;
    @Schema(description = "true si la zona está activa", example = "true")
    private Boolean estado;
    @Schema(description = "Incidentes por km² desde los que la zona es de riesgo ALTO", example = "15")
    private Integer umbral_alto_min;
    @Schema(description = "Incidentes por km² desde los que la zona es de riesgo MEDIO", example = "6")
    private Integer umbral_medio_min;
    @Schema(description = "Máximo de incidentes por km² del riesgo MEDIO", example = "14")
    private Integer umbral_medio_max;
    @Schema(description = "Máximo de incidentes por km² del riesgo BAJO", example = "5")
    private Integer umbral_bajo_max;
    @Schema(description = "Días hacia atrás que se consideran para clasificar la zona", example = "30")
    private Integer ventana_dias;
    @Schema(description = "Radio de la geocerca en metros", example = "300")
    private Double radio_geocerca;
}
