package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LecturaSensorDTO {
    private Long id_lectura;
    private String tipo_lectura;
    private Double valor;
    private String unidad;
    private LocalDateTime fecha_hora;
    private Boolean anomalia;
    private Long id_dispositivo;
}
