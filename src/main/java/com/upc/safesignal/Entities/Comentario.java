package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentario")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario")
    private Long id_comentario;

    @Column(name = "texto", length = 500)
    private String texto;

    @Column(name = "fecha_hora")
    private LocalDateTime fecha_hora;

    @ManyToOne
    @JoinColumn(name = "id_incidente")
    private Incidente incidente;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}