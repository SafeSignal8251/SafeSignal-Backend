package com.upc.safesignal.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id_usuario;
    private String nombres;
    private String apellidos;
    private String correo;

    // se recibe en el registro y el login, pero nunca se devuelve en las respuestas
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;

    private String telefono;
    private LocalDateTime fecha_registro;
    private String estado;
    private String dni;
    private String distrito;
    private String rol;
    private String motivo_suspension;
    private String direccion;
    private String mensaje_sos;
}
