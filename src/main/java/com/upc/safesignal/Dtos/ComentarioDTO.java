package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioDTO {
    @Schema(description = "Identificador de el comentario", example = "1")
    private Long id_comentario;
    @Schema(description = "Texto del comentario (obligatorio, máximo 500 caracteres)", example = "Confirmo, también lo vi esta tarde.")
    private String texto;
    @Schema(description = "Fecha y hora del registro. La asigna el servidor.", example = "2026-11-27T22:15:00")
    private LocalDateTime fecha_hora;
    @Schema(description = "Identificador de el incidente", example = "1")
    private Long id_incidente;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
}
