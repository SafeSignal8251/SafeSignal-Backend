package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncidenteDTO {
    @Schema(description = "Identificador de el incidente", example = "1")
    private Long id_incidente;
    @Schema(description = "Tipo de incidente", example = "Robo / Asalto a mano armada")
    private String tipo_incidente;
    @Schema(description = "Descripción del contenido", example = "Robo al paso cerca del paradero, dos personas en moto.")
    private String descripcion;
    @Schema(description = "Latitud en grados decimales", example = "-12.0464")
    private Double latitud;
    @Schema(description = "Longitud en grados decimales", example = "-77.0428")
    private Double longitud;
    @Schema(description = "Fecha y hora del incidente. La asigna el servidor.", example = "2026-11-27T22:15:00")
    private LocalDateTime fecha_hora;
    @Schema(description = "Nivel de riesgo: BAJO, MEDIO o ALTO", example = "MEDIO")
    private String nivel_riesgo;
    @Schema(description = "Estado del reporte: PENDIENTE, VERIFICADO o ELIMINADO", example = "PENDIENTE")
    private String estado;
    @Schema(description = "Dirección del incidente (máximo 150 caracteres)", example = "Avenida Universitaria 1801, San Miguel")
    private String direccion;
    @Schema(description = "Distrito", example = "San Miguel")
    private String distrito;
    @Schema(description = "Identificador de la zona de riesgo", example = "1")
    private Long id_zona;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
    // true si el reporte ya fue validado por la comunidad o verificado por el administrador (ya no se puede editar)
    @Schema(description = "Indica si el reporte ya fue validado", example = "false")
    private Boolean validado;
}
