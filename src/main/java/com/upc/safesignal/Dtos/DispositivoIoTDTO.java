package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoIoTDTO {
    @Schema(description = "Identificador de el dispositivo IoT", example = "1")
    private Long id_dispositivo;
    @Schema(description = "Tipo de dispositivo", example = "Botón de pánico")
    private String tipo_dispositivo;
    @Schema(description = "Nombre descriptivo", example = "Mi botón")
    private String nombre;
    @Schema(description = "Código único del dispositivo (no puede repetirse)", example = "BTN-0001")
    private String codigo_dispositivo;
    @Schema(description = "Estado: HABILITADO o DESHABILITADO", example = "HABILITADO")
    private String estado;
    @Schema(description = "Nivel de batería en porcentaje (0 a 100)", example = "85")
    private Integer nivel_bateria;
    @Schema(description = "Fecha de vinculación. La asigna el servidor.", example = "2026-11-27T20:00:00")
    private LocalDateTime fecha_vinculacion;
    @Schema(description = "Fecha de la última sincronización. La asigna el servidor.", example = "2026-11-27T21:00:00")
    private LocalDateTime fecha_ultima;
    @Schema(description = "Tipos de anomalía a detectar, separados por comas", example = "DESVIO_RUTA,VELOCIDAD_ALTA")
    private String tipos_anomalia;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
}
