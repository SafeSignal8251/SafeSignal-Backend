package com.upc.safesignal.Security.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    private String correo;
    private String contrasena;
}
