package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionDTO {
    @Schema(description = "Identificador de la ubicación", example = "1")
    private Long id_ubicacion;
    @Schema(description = "Latitud en grados decimales", example = "-12.0464")
    private Double latitud;
    @Schema(description = "Longitud en grados decimales", example = "-77.0428")
    private Double longitud;
    @Schema(description = "Fecha y hora del punto. La asigna el servidor.", example = "2026-11-27T22:16:00")
    private LocalDateTime fecha_hora;
    @Schema(description = "Precisión del punto en metros", example = "20")
    private Double precision_metros;
    @Schema(description = "Identificador de el seguimiento", example = "1")
    private Long id_seguimiento;
}
