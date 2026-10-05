package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoDTO {
    private Long id_seguimiento;
    private LocalDateTime fecha_inicio;
    private LocalDateTime fecha_fin;
    private Integer tiempo_monitoreo;
    private String estado;
    private String destino;
    private Long id_usuario;
}
