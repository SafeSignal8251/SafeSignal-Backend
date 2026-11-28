package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    @Schema(description = "Identificador de la notificación", example = "1")
    private Long id_notificacion;
    @Schema(description = "Texto de la notificación (máximo 255 caracteres)", example = "ALERTA SOS de Alexis Rodríguez")
    private String mensaje;
    @Schema(description = "Fecha de envío. La asigna el servidor.", example = "2026-11-27T22:15:00")
    private LocalDateTime fecha_envio;
    @Schema(description = "Estado: ENVIADA o LEIDA", example = "ENVIADA")
    private String estado;
    @Schema(description = "Tipo de notificación: ALERTA o AVISO", example = "ALERTA")
    private String tipo;
    @Schema(description = "Severidad del aviso: Informativo, Preventivo o Urgente", example = "Preventivo")
    private String severidad;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
    @Schema(description = "Identificador de la zona de riesgo", example = "1")
    private Long id_zona;
    @Schema(description = "Identificador de la alerta", example = "1")
    private Long id_alerta;
    @Schema(description = "Identificador de el contacto de confianza", example = "1")
    private Long id_contacto;
}
