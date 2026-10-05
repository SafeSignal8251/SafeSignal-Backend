package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long id_notificacion;

    @Column(name = "mensaje", length = 255)
    private String mensaje;

    @Column(name = "fecha_envio")
    private LocalDateTime fecha_envio;

    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "tipo", length = 30)
    private String tipo;

    @Column(name = "severidad", length = 20)
    private String severidad;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_zona")
    private ZonaRiesgo zona;

    @ManyToOne
    @JoinColumn(name = "id_alerta")
    private Alerta alerta;

    @ManyToOne
    @JoinColumn(name = "id_contacto")
    private ContactoConfianza contacto;
}