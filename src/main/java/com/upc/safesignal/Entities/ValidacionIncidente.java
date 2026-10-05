package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "validacion_incidente")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ValidacionIncidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_validacion")
    private Long id_validacion;

    @Column(name = "tipo_voto", length = 20)
    private String tipo_voto;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "id_incidente")
    private Incidente incidente;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}