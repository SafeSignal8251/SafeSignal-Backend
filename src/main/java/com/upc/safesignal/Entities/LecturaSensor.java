package com.upc.safesignal.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lectura_sensor")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LecturaSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lectura")
    private Long id_lectura;

    @Column(name = "tipo_lectura", length = 50)
    private String tipo_lectura;

    @Column(name = "valor")
    private Double valor;

    @Column(name = "unidad", length = 20)
    private String unidad;

    @Column(name = "fecha_hora")
    private LocalDateTime fecha_hora;

    @Column(name = "anomalia")
    private Boolean anomalia;

    @ManyToOne
    @JoinColumn(name = "id_dispositivo")
    private DispositivoIoT dispositivo;
}