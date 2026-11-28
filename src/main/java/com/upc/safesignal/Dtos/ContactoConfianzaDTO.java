package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactoConfianzaDTO {
    @Schema(description = "Identificador de el contacto de confianza", example = "1")
    private Long id_contacto;
    @Schema(description = "Nombres", example = "Alexis")
    private String nombres;
    @Schema(description = "Apellidos", example = "Rodríguez Pérez")
    private String apellidos;
    @Schema(description = "Teléfono móvil de 9 dígitos", example = "987654321")
    private String telefono;
    @Schema(description = "Relación con el usuario", example = "Madre")
    private String parentesco;
    @Schema(description = "Prioridad de aviso (1 es la más alta)", example = "1")
    private Integer prioridad;
    @Schema(description = "Indica si el contacto recibe avisos por SMS", example = "true")
    private Boolean notificar_sms;
    @Schema(description = "Indica si el contacto recibe avisos por WhatsApp", example = "true")
    private Boolean notificar_whatsapp;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
}
