package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "estadistica_publicada")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EstadisticaPublicada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estadistica")
    private Long id_estadistica;

    @Column(name = "codigo", length = 30, unique = true)
    private String codigo;

    @Column(name = "titulo", length = 150)
    private String titulo;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "categoria", length = 50)
    private String categoria;

    @Column(name = "parametros", length = 500)
    private String parametros;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fecha_publicacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}