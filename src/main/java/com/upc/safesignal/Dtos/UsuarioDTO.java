package com.upc.safesignal.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    @Schema(description = "Identificador de el usuario", example = "1")
    private Long id_usuario;
    @Schema(description = "Nombres", example = "Alexis")
    private String nombres;
    @Schema(description = "Apellidos", example = "Rodríguez Pérez")
    private String apellidos;
    @Schema(description = "Correo electrónico", example = "alexis@correo.com")
    private String correo;

    // se recibe en el registro y el login, pero nunca se devuelve en las respuestas
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Contraseña (mínimo 8 caracteres); nunca se devuelve en las respuestas", example = "Clave1234")
    private String contrasena;

    @Schema(description = "Teléfono móvil de 9 dígitos", example = "987654321")
    private String telefono;
    @Schema(description = "Fecha de registro de la cuenta. La asigna el servidor.", example = "2026-11-24T10:00:00")
    private LocalDateTime fecha_registro;
    @Schema(description = "Estado de la cuenta: ACTIVA o SUSPENDIDA", example = "ACTIVA")
    private String estado;
    @Schema(description = "DNI de 8 dígitos", example = "70123456")
    private String dni;
    @Schema(description = "Distrito", example = "San Miguel")
    private String distrito;
    @Schema(description = "Rol de la cuenta: CLIENTE o ADMIN", example = "CLIENTE")
    private String rol;
    @Schema(description = "Motivo de la suspensión de la cuenta", example = "Reportes falsos repetidos")
    private String motivo_suspension;
    @Schema(description = "Dirección o referencia del lugar", example = "Av. Universitaria 1801, San Miguel")
    private String direccion;
    @Schema(description = "Mensaje que reciben los contactos al activar el SOS (máximo 255 caracteres)", example = "Estoy en peligro, llámame.")
    private String mensaje_sos;
}
