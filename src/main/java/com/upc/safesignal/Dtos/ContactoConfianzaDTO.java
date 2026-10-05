package com.upc.safesignal.Dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactoConfianzaDTO {
    private Long id_contacto;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String parentesco;
    private Integer prioridad;
    private Boolean notificar_sms;
    private Boolean notificar_whatsapp;
    private Long id_usuario;
}
