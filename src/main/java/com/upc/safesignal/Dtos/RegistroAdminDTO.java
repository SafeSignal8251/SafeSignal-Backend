package com.upc.safesignal.Dtos;

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
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
    private String apellidos;

    @NotBlank(message = "El correo institucional es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    private String correo;

    // Código de placa / Id operativo: también es el identificador con el que el administrador inicia sesión
    @NotBlank(message = "El código de placa / Id operativo es obligatorio")
    @Pattern(regexp = "^[0-9A-Za-z]{8,15}$", message = "El código de placa / Id operativo debe tener entre 8 y 15 caracteres alfanuméricos")
    private String codigo_placa;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telefono;

    // Municipalidad o central de monitoreo
    @NotBlank(message = "La municipalidad o central de monitoreo es obligatoria")
    @Size(max = 50, message = "La municipalidad o central no puede superar los 50 caracteres")
    private String entidad;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    private String contrasena;

    // Código que entrega la institución: sin él cualquiera podría crearse una cuenta de administrador
    @NotBlank(message = "El código de autorización institucional es obligatorio")
    private String codigo_autorizacion;
}
