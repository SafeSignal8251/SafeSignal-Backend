package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "filtro_guardado")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FiltroGuardado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_filtro")
    private Long id_filtro;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "distrito", length = 50)
    private String distrito;

    @Column(name = "tipologia", length = 50)
    private String tipologia;

    @Column(name = "rango_dias")
    private Integer rango_dias;

    @Column(name = "fecha_creacion")
    private LocalDateTime fecha_creacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}