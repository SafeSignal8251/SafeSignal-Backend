package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionDTO {
    private Long id_ubicacion;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fecha_hora;
    private Double precision_metros;
    private Long id_seguimiento;
}
