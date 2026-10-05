package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerta")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Long id_alerta;

    @Column(name = "tipo_alerta", length = 30)
    private String tipo_alerta;

    @Column(name = "fecha_hora")
    private LocalDateTime fecha_hora;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "origen", length = 30)
    private String origen;

    @Column(name = "estado", length = 30)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "id_dispositivo")
    private DispositivoIoT dispositivo;

    @Column(name = "direccion", length = 150)
    private String direccion;

    @Column(name = "codigo_despacho", length = 20)
    private String codigo_despacho;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fecha_cancelacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}