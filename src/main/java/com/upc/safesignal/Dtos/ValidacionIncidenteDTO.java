package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionIncidenteDTO {
    private Long id_validacion;
    private String tipo_voto;
    private LocalDateTime fecha;
    private Long id_incidente;
    private Long id_usuario;
}
