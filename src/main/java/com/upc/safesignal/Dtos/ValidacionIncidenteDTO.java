package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionIncidenteDTO {
    @Schema(description = "Identificador de la validación", example = "1")
    private Long id_validacion;
    @Schema(description = "Tipo de voto: CONFIRMADO o FALSA_ALARMA", example = "CONFIRMADO")
    private String tipo_voto;
    @Schema(description = "Fecha del voto. La asigna el servidor.", example = "2026-11-27T22:10:00")
    private LocalDateTime fecha;
    @Schema(description = "Identificador de el incidente", example = "1")
    private Long id_incidente;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
}
