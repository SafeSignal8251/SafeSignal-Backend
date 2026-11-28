package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Datos de la HU-01 (crear cuenta de administrador). Se validan con @Valid en el controller.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAdminDTO {

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden superar los 100 caracteres")
    @Schema(description = "Nombres", example = "Alexis")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
    @Schema(description = "Apellidos", example = "Rodríguez Pérez")
    private String apellidos;

    @NotBlank(message = "El correo institucional es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    @Schema(description = "Correo electrónico", example = "alexis@correo.com")
    private String correo;

    // Código de placa / Id operativo: también es el identificador con el que el administrador inicia sesión
    @NotBlank(message = "El código de placa / Id operativo es obligatorio")
    @Pattern(regexp = "^[0-9A-Za-z]{8,15}$", message = "El código de placa / Id operativo debe tener entre 8 y 15 caracteres alfanuméricos")
    @Schema(description = "Código de placa o id operativo de la cuenta institucional", example = "PNP-1234")
    private String codigo_placa;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    @Schema(description = "Teléfono móvil de 9 dígitos", example = "987654321")
    private String telefono;

    // Municipalidad o central de monitoreo
    @NotBlank(message = "La municipalidad o central de monitoreo es obligatoria")
    @Size(max = 50, message = "La municipalidad o central no puede superar los 50 caracteres")
    @Schema(description = "Municipalidad o central de monitoreo", example = "Municipalidad de San Miguel")
    private String entidad;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    @Schema(description = "Contraseña (mínimo 8 caracteres)", example = "Clave1234")
    private String contrasena;

    // Código que entrega la institución: sin él cualquiera podría crearse una cuenta de administrador
    @NotBlank(message = "El código de autorización institucional es obligatorio")
    @Schema(description = "Código de autorización institucional para crear administradores", example = "AUT-2026")
    private String codigo_autorizacion;
}
