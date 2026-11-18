package com.upc.safesignal.security;

import com.upc.safesignal.Entities.Usuario;
import com.upc.safesignal.Repositories.UsuarioRepository;
import com.upc.safesignal.Security.services.CustomUserDetailsService;
import com.upc.safesignal.Security.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 Prueba de integración de la seguridad (JWT + @PreAuthorize) contra la API real.
 Requiere PostgreSQL en ejecución, igual que SafeSignalApplicationTests.
*/
@SpringBootTest
class AutorizacionIntegrationTest {

    @Autowired
    private WebApplicationContext contexto;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    private MockMvc mvc;
    private Usuario ana;
    private Usuario beto;
    private String tokenAna;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
        long sufijo = System.nanoTime();
        ana = crear("ana" + sufijo + "@test.com", "A" + sufijo);
        beto = crear("beto" + sufijo + "@test.com", "B" + sufijo);
        tokenAna = jwtUtil.generateToken(userDetailsService.loadUserByUsername(ana.getCorreo()), ana.getId_usuario());
    }

    @AfterEach
    void limpiar() {
        usuarioRepository.delete(ana);
        usuarioRepository.delete(beto);
    }

    private Usuario crear(String correo, String dni) {
        Usuario u = new Usuario();
        u.setNombres("Test");
        u.setApellidos("Seguridad");
        u.setCorreo(correo);
        u.setDni(dni.substring(0, Math.min(15, dni.length())));
        u.setContrasena(passwordEncoder.encode("Clave12345"));
        u.setRol("CLIENTE");
        u.setEstado("ACTIVA");
        u.setFecha_registro(LocalDateTime.now());
        return usuarioRepository.save(u);
    }

    @Test
    void sinTokenResponde401() throws Exception {
        mvc.perform(get("/api/usuarios/" + ana.getId_usuario())).andExpect(status().isUnauthorized());
    }

    @Test
    void unUsuarioPuedeVerSusPropiosDatos() throws Exception {
        mvc.perform(get("/api/contactos-confianza/usuario/" + ana.getId_usuario())
                .header("Authorization", "Bearer " + tokenAna)).andExpect(status().isOk());
    }

    @Test
    void unUsuarioNoPuedeVerDatosDeOtro() throws Exception {
        mvc.perform(get("/api/contactos-confianza/usuario/" + beto.getId_usuario())
                .header("Authorization", "Bearer " + tokenAna)).andExpect(status().isForbidden());
        mvc.perform(get("/api/usuarios/" + beto.getId_usuario())
                .header("Authorization", "Bearer " + tokenAna)).andExpect(status().isForbidden());
    }

    @Test
    void unUsuarioNoPuedeCrearRegistrosEnNombreDeOtro() throws Exception {
        mvc.perform(post("/api/contactos-confianza").param("id_usuario", String.valueOf(beto.getId_usuario()))
                .header("Authorization", "Bearer " + tokenAna)
                .contentType("application/json")
                .content("{\"nombres\":\"X\",\"telefono\":\"999000111\"}")).andExpect(status().isForbidden());
    }

    @Test
    void unUsuarioNoPuedeBorrarOEditarOtraCuenta() throws Exception {
        mvc.perform(delete("/api/usuarios/" + beto.getId_usuario())
                .header("Authorization", "Bearer " + tokenAna)).andExpect(status().isForbidden());
        mvc.perform(put("/api/usuarios/" + beto.getId_usuario())
                .header("Authorization", "Bearer " + tokenAna)
                .contentType("application/json").content("{\"nombres\":\"Hack\"}")).andExpect(status().isForbidden());
    }

    @Test
    void unClienteNoPuedeUsarEndpointsDeAdministrador() throws Exception {
        mvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + tokenAna))
                .andExpect(status().isForbidden());
    }

    @Test
    void elPreflightCorsDelFrontendNoSeRechazaEnRutasProtegidas() throws Exception {
        mvc.perform(options("/api/alertas")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "authorization"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void elRegistroConDatosInvalidosResponde400() throws Exception {
        mvc.perform(post("/api/usuarios/registrar").contentType("application/json")
                .content("{\"nombres\":\"\",\"correo\":\"mal\",\"contrasena\":\"1\"}"))
                .andExpect(status().isBadRequest());
    }
}
