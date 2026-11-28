package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RutaDTO {
    @Schema(description = "Identificador de la ruta", example = "1")
    private Long id_ruta;
    @Schema(description = "Nombre de la ruta (máximo 100 caracteres)", example = "Plaza San Miguel → Av. Universitaria")
    private String nombre;
    @Schema(description = "Punto de origen", example = "Plaza San Miguel")
    private String origen;
    @Schema(description = "Punto de destino", example = "Avenida Universitaria 1801, San Miguel")
    private String destino;
    @Schema(description = "Latitud del origen", example = "-12.0777")
    private Double origen_latitud;
    @Schema(description = "Longitud del origen", example = "-77.0834")
    private Double origen_longitud;
    @Schema(description = "Latitud del destino", example = "-12.0680")
    private Double destino_latitud;
    @Schema(description = "Longitud del destino", example = "-77.0780")
    private Double destino_longitud;
    @Schema(description = "Distancia en kilómetros", example = "4.9")
    private Double distancia;
    @Schema(description = "Tiempo estimado a pie, en minutos", example = "59")
    private Integer tiempo_estimado;
    @Schema(description = "Tipo de ruta", example = "PEATONAL")
    private String tipo_ruta;
    @Schema(description = "Fecha de creación. La asigna el servidor.", example = "2026-11-27T22:00:00")
    private LocalDateTime fecha_creacion;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
}
