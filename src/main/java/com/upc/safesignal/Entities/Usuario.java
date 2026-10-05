package com.upc.safesignal.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id_usuario;

    @Column(name = "nombres", length = 100)
    private String nombres;

    @Column(name = "apellidos", length = 100)
    private String apellidos;

    @Column(name = "correo", length = 100)
    private String correo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "contrasena", length = 255)
    private String contrasena;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "fecha_registro")
    private LocalDateTime fecha_registro;

    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "dni", length = 15)
    private String dni;

    @Column(name = "distrito", length = 50)
    private String distrito;

    @Column(name = "rol", length = 20)
    private String rol;

    @Column(name = "motivo_suspension", length = 255)
    private String motivo_suspension;

    @Column(name = "direccion", length = 150)
    private String direccion;

    // HU-49: mensaje predeterminado de la alerta SOS
    @Column(name = "mensaje_sos", length = 255)
    private String mensaje_sos;
}