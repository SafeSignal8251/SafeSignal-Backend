package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FiltroGuardadoDTO {
    private Long id_filtro;
    private String nombre;
    private String distrito;
    private String tipologia;
    private Integer rango_dias;
    private LocalDateTime fecha_creacion;
    private Long id_usuario;
}
