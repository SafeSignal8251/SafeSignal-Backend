package com.upc.safesignal.security;

import com.upc.safesignal.Entities.Usuario;
import com.upc.safesignal.Repositories.UsuarioRepository;
import com.upc.safesignal.Security.util.Seguridad;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// Pruebas de autorización a nivel de objeto (evita actuar como otro usuario)
@ExtendWith(MockitoExtension.class)
class SeguridadTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private Seguridad seguridad;

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private void autenticarComo(String correo) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(correo, null, List.of()));
    }

    @Test
    void permiteElPropioId() {
        Usuario ana = new Usuario();
        ana.setId_usuario(7L);
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(List.of(ana));
        autenticarComo("ana@correo.com");

        assertTrue(seguridad.esUsuario(7L));
    }

    @Test
    void rechazaElIdDeOtroUsuario() {
        Usuario ana = new Usuario();
        ana.setId_usuario(7L);
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(List.of(ana));
        autenticarComo("ana@correo.com");

        assertFalse(seguridad.esUsuario(8L));
    }

    @Test
    void rechazaSiNoHaySesion() {
        assertFalse(seguridad.esUsuario(7L));
    }

    @Test
    void rechazaUnIdNulo() {
        assertFalse(seguridad.esUsuario(null));
    }
}
