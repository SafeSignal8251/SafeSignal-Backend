package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "suscripcion_zona")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SuscripcionZona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_suscripcion")
    private Long id_suscripcion;

    @Column(name = "fecha_suscripcion")
    private LocalDateTime fecha_suscripcion;

    @Column(name = "etiqueta", length = 100)
    private String etiqueta;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "radio_metros")
    private Double radio_metros;

    @Column(name = "categorias", length = 255)
    private String categorias;

    @Column(name = "notificar_push")
    private Boolean notificar_push;

    @Column(name = "notificar_whatsapp_sms")
    private Boolean notificar_whatsapp_sms;

    @Column(name = "notificar_correo")
    private Boolean notificar_correo;

    @Column(name = "hora_desde")
    private LocalTime hora_desde;

    @Column(name = "hora_hasta")
    private LocalTime hora_hasta;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_zona")
    private ZonaRiesgo zona;
}