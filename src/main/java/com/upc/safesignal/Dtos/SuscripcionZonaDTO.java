package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionZonaDTO {
    @Schema(description = "Identificador de la suscripción", example = "1")
    private Long id_suscripcion;
    @Schema(description = "Fecha de suscripción. La asigna el servidor.", example = "2026-11-27T22:00:00")
    private LocalDateTime fecha_suscripcion;
    @Schema(description = "Etiqueta que el usuario le pone a la zona", example = "Mi barrio")
    private String etiqueta;
    @Schema(description = "Latitud del punto de interés", example = "-12.0777")
    private Double latitud;
    @Schema(description = "Longitud del punto de interés", example = "-77.0834")
    private Double longitud;
    @Schema(description = "Radio en metros", example = "300")
    private Double radio_metros;
    @Schema(description = "Categorías de incidente de interés, separadas por comas", example = "Robo,Acoso")
    private String categorias;
    @Schema(description = "Recibir avisos push", example = "true")
    private Boolean notificar_push;
    @Schema(description = "Recibir avisos por WhatsApp o SMS", example = "false")
    private Boolean notificar_whatsapp_sms;
    @Schema(description = "Recibir avisos por correo", example = "false")
    private Boolean notificar_correo;
    @Schema(description = "Hora desde la que se reciben avisos", example = "18:00:00")
    private LocalTime hora_desde;
    @Schema(description = "Hora hasta la que se reciben avisos", example = "23:59:00")
    private LocalTime hora_hasta;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
    @Schema(description = "Identificador de la zona de riesgo", example = "1")
    private Long id_zona;
}
