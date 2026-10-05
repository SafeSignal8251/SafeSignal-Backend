package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private Long id_notificacion;
    private String mensaje;
    private LocalDateTime fecha_envio;
    private String estado;
    private String tipo;
    private String severidad;
    private Long id_usuario;
    private Long id_zona;
    private Long id_alerta;
    private Long id_contacto;
}
