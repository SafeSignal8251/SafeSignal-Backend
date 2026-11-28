package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FiltroGuardadoDTO {
    @Schema(description = "Identificador de el filtro guardado", example = "1")
    private Long id_filtro;
    @Schema(description = "Nombre del filtro (no se repite para el mismo usuario)", example = "San Miguel últimos 30 días")
    private String nombre;
    @Schema(description = "Distrito", example = "San Miguel")
    private String distrito;
    @Schema(description = "Tipo de incidente por el que se filtra", example = "Robo / Asalto a mano armada")
    private String tipologia;
    @Schema(description = "Rango de días hacia atrás", example = "30")
    private Integer rango_dias;
    @Schema(description = "Fecha de creación. La asigna el servidor.", example = "2026-11-27T22:00:00")
    private LocalDateTime fecha_creacion;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
}
