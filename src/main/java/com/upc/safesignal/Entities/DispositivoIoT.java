package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispositivo_iot")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DispositivoIoT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dispositivo")
    private Long id_dispositivo;

    @Column(name = "tipo_dispositivo", length = 50)
    private String tipo_dispositivo;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "codigo_dispositivo", length = 100)
    private String codigo_dispositivo;

    @Column(name = "estado", length = 30)
    private String estado;

    @Column(name = "nivel_bateria")
    private Integer nivel_bateria;

    @Column(name = "fecha_vinculacion")
    private LocalDateTime fecha_vinculacion;

    @Column(name = "fecha_ultima")
    private LocalDateTime fecha_ultima;

    @Column(name = "tipos_anomalia", length = 255)
    private String tipos_anomalia;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}