package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertaDTO {
    private Long id_alerta;
    private String tipo_alerta;
    private LocalDateTime fecha_hora;
    private Double latitud;
    private Double longitud;
    private String origen;
    private String estado;
    private String direccion;
    private String codigo_despacho;
    private LocalDateTime fecha_cancelacion;
    private Long id_dispositivo;
    private Long id_usuario;
}
