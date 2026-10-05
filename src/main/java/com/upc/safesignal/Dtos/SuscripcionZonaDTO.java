package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionZonaDTO {
    private Long id_suscripcion;
    private LocalDateTime fecha_suscripcion;
    private String etiqueta;
    private Double latitud;
    private Double longitud;
    private Double radio_metros;
    private String categorias;
    private Boolean notificar_push;
    private Boolean notificar_whatsapp_sms;
    private Boolean notificar_correo;
    private LocalTime hora_desde;
    private LocalTime hora_hasta;
    private Long id_usuario;
    private Long id_zona;
}
