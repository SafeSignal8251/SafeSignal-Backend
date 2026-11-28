package com.upc.safesignal.Security.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
public class AuthResponseDTO {
    @Schema(description = "Token JWT que se envía como Bearer en las demás peticiones", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String jwt;
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
    @Schema(description = "Roles de la cuenta", example = "ROLE_CLIENTE")
    private Set<String> roles;
}
