package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoIoTDTO {
    private Long id_dispositivo;
    private String tipo_dispositivo;
    private String nombre;
    private String codigo_dispositivo;
    private String estado;
    private Integer nivel_bateria;
    private LocalDateTime fecha_vinculacion;
    private LocalDateTime fecha_ultima;
    private String tipos_anomalia;
    private Long id_usuario;
}
