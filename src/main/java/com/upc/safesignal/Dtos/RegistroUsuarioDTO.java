package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

// Datos de la HU-01 (crear cuenta). Se validan con @Valid en el controller.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroUsuarioDTO {

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden superar los 100 caracteres")
    @Schema(description = "Nombres", example = "Alexis")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
    @Schema(description = "Apellidos", example = "Rodríguez Pérez")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    @Schema(description = "Correo electrónico", example = "alexis@correo.com")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    @Schema(description = "Contraseña (mínimo 8 caracteres)", example = "Clave1234")
    private String contrasena;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9A-Za-z]{8,15}$", message = "El DNI debe tener entre 8 y 15 caracteres alfanuméricos")
    @Schema(description = "DNI de 8 dígitos", example = "70123456")
    private String dni;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    @Schema(description = "Teléfono móvil de 9 dígitos", example = "987654321")
    private String telefono;

    @Size(max = 50, message = "El distrito no puede superar los 50 caracteres")
    @Schema(description = "Distrito", example = "San Miguel")
    private String distrito;

    @Size(max = 150, message = "La dirección no puede superar los 150 caracteres")
    @Schema(description = "Dirección o referencia del lugar", example = "Av. Universitaria 1801, San Miguel")
    private String direccion;
}
