package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticaPublicadaDTO {
    private Long id_estadistica;
    private String codigo;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String parametros;
    private LocalDateTime fecha_publicacion;
    private Long id_usuario;
}
