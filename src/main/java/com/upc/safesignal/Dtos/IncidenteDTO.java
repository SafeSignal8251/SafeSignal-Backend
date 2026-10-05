package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncidenteDTO {
    private Long id_incidente;
    private String tipo_incidente;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fecha_hora;
    private String nivel_riesgo;
    private String estado;
    private String direccion;
    private String distrito;
    private Long id_zona;
    private Long id_usuario;
}
