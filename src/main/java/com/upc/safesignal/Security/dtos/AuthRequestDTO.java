package com.upc.safesignal.Security.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {

    @NotBlank(message = "El correo es obligatorio")
    @Schema(description = "Correo electrónico de la cuenta", example = "alexis@correo.com")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña de la cuenta", example = "Clave1234")
    private String contrasena;
}
