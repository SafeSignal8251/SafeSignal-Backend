package com.upc.safesignal.Dtos;

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
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    private String contrasena;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9A-Za-z]{8,15}$", message = "El DNI debe tener entre 8 y 15 caracteres alfanuméricos")
    private String dni;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telefono;

    @Size(max = 50, message = "El distrito no puede superar los 50 caracteres")
    private String distrito;

    @Size(max = 150, message = "La dirección no puede superar los 150 caracteres")
    private String direccion;
}
