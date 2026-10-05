package com.upc.safesignal.Dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioDTO {
    private Long id_comentario;
    private String texto;
    private LocalDateTime fecha_hora;
    private Long id_incidente;
    private Long id_usuario;
}
