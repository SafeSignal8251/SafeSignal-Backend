package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticaPublicadaDTO {
    @Schema(description = "Identificador de la estadística", example = "1")
    private Long id_estadistica;
    @Schema(description = "Código generado por el sistema", example = "REP-2026-1")
    private String codigo;
    @Schema(description = "Título de la estadística", example = "Incidentes de la semana")
    private String titulo;
    @Schema(description = "Descripción del contenido", example = "Robo al paso cerca del paradero, dos personas en moto.")
    private String descripcion;
    @Schema(description = "Categoría de la estadística", example = "Territorial")
    private String categoria;
    @Schema(description = "Parámetros de cálculo en texto", example = "distrito=San Miguel;dias=30")
    private String parametros;
    @Schema(description = "Fecha de publicación. La asigna el servidor.", example = "2026-11-27T22:00:00")
    private LocalDateTime fecha_publicacion;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
}
