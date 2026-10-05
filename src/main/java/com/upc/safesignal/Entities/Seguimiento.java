package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "seguimiento")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento")
    private Long id_seguimiento;

    @Column(name = "fecha_inicio")
    private LocalDateTime fecha_inicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fecha_fin;

    @Column(name = "tiempo_monitoreo")
    private Integer tiempo_monitoreo;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "destino", length = 50)
    private String destino;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}