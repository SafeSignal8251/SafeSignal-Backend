package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertaDTO {
    @Schema(description = "Identificador de la alerta", example = "1")
    private Long id_alerta;
    @Schema(description = "Tipo de alerta: SOS, SILENCIOSA o ANOMALIA", example = "SOS")
    private String tipo_alerta;
    @Schema(description = "Fecha y hora del registro. La asigna el servidor.", example = "2026-11-27T22:15:00")
    private LocalDateTime fecha_hora;
    @Schema(description = "Latitud en grados decimales", example = "-12.0464")
    private Double latitud;
    @Schema(description = "Longitud en grados decimales", example = "-77.0428")
    private Double longitud;
    @Schema(description = "Origen de la alerta: APP o DISPOSITIVO", example = "APP")
    private String origen;
    @Schema(description = "Estado: ACTIVA, CANCELADA o DESPACHADA. Lo asigna el servidor", example = "ACTIVA")
    private String estado;
    @Schema(description = "Dirección o referencia del lugar", example = "Av. Universitaria 1801, San Miguel")
    private String direccion;
    @Schema(description = "Código de la unidad que atiende la alerta (lo asigna el administrador al despachar)", example = "SER-1024")
    private String codigo_despacho;
    @Schema(description = "Fecha y hora de cancelación. La asigna el servidor.", example = "2026-11-27T22:20:00")
    private LocalDateTime fecha_cancelacion;
    @Schema(description = "Identificador de el dispositivo IoT", example = "1")
    private Long id_dispositivo;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
}
