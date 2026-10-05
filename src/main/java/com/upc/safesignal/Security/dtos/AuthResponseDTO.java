package com.upc.safesignal.Security.dtos;

import lombok.Data;

import java.util.Set;

@Data
public class AuthResponseDTO {
    private String jwt;
    private Long id_usuario;
    private Set<String> roles;
}
