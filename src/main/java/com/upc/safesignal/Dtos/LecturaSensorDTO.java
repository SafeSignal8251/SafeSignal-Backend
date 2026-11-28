package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LecturaSensorDTO {
    @Schema(description = "Identificador de la lectura", example = "1")
    private Long id_lectura;
    @Schema(description = "Magnitud medida por el sensor", example = "ACELEROMETRO")
    private String tipo_lectura;
    @Schema(description = "Valor medido (obligatorio)", example = "9.8")
    private Double valor;
    @Schema(description = "Unidad de medida", example = "m/s2")
    private String unidad;
    @Schema(description = "Fecha y hora del registro. La asigna el servidor.", example = "2026-11-27T22:15:00")
    private LocalDateTime fecha_hora;
    @Schema(description = "true si la lectura es anómala: genera una alerta automática", example = "true")
    private Boolean anomalia;
    @Schema(description = "Identificador de el dispositivo IoT", example = "1")
    private Long id_dispositivo;
}
