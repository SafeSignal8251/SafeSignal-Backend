package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RutaDTO {
    private Long id_ruta;
    private String nombre;
    private String origen;
    private String destino;
    private Double origen_latitud;
    private Double origen_longitud;
    private Double destino_latitud;
    private Double destino_longitud;
    private Double distancia;
    private Integer tiempo_estimado;
    private String tipo_ruta;
    private LocalDateTime fecha_creacion;
    private Long id_usuario;
}
