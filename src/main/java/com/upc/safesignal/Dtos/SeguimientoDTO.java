package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoDTO {
    @Schema(description = "Identificador de el seguimiento", example = "1")
    private Long id_seguimiento;
    @Schema(description = "Fecha de inicio. La asigna el servidor.", example = "2026-11-27T22:00:00")
    private LocalDateTime fecha_inicio;
    @Schema(description = "Fecha de fin. La asigna el servidor.", example = "2026-11-27T22:40:00")
    private LocalDateTime fecha_fin;
    @Schema(description = "Duración del monitoreo en minutos (mayor que 0)", example = "60")
    private Integer tiempo_monitoreo;
    @Schema(description = "Estado: ACTIVO o FINALIZADO. Lo asigna el servidor", example = "ACTIVO")
    private String estado;
    @Schema(description = "Destino o motivo del seguimiento (máximo 50 caracteres)", example = "Compartir ubicación")
    private String destino;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
}
