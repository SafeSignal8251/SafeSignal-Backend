package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contacto_confianza")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ContactoConfianza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contacto")
    private Long id_contacto;

    @Column(name = "nombres", length = 100)
    private String nombres;

    @Column(name = "apellidos", length = 100)
    private String apellidos;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "parentesco", length = 50)
    private String parentesco;

    @Column(name = "prioridad")
    private Integer prioridad;

    @Column(name = "notificar_sms")
    private Boolean notificar_sms;

    @Column(name = "notificar_whatsapp")
    private Boolean notificar_whatsapp;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}