package com.upc.safesignal.security;

import com.upc.safesignal.Entities.Usuario;
import com.upc.safesignal.Repositories.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 Pruebas de la HU-01 (escenario 2): la cuenta de administrador se crea desde el registro,
 pero solo con el código de autorización institucional. Requiere PostgreSQL en ejecución.
*/
@SpringBootTest
class RegistroInstitucionalIntegrationTest {

    @Autowired
    private WebApplicationContext contexto;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Value("${safesignal.admin.codigo-registro}")
    private String codigoCorrecto;

    private MockMvc mvc;
    private String correo;
    private String placa;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
        long n = System.nanoTime();
        correo = "admin.test" + n + "@municipalidad.gob.pe";
        placa = ("P" + n).substring(0, 12);
    }

    @AfterEach
    void limpiar() {
        usuarioRepository.findByCorreo(correo).forEach(usuarioRepository::delete);
    }

    private String cuerpo(String codigoAutorizacion) {
        return "{\"nombres\":\"Rosa\",\"apellidos\":\"Quispe\",\"correo\":\"" + correo + "\",\"codigo_placa\":\"" + placa
                + "\",\"telefono\":\"987654321\",\"entidad\":\"Municipalidad de Lima\",\"contrasena\":\"Clave12345\","
                + "\"codigo_autorizacion\":\"" + codigoAutorizacion + "\"}";
    }

    @Test
    void conElCodigoCorrectoSeCreaUnAdministradorYSuPlacaEsSuIdentificador() throws Exception {
        mvc.perform(post("/api/usuarios/registrar-institucional").contentType("application/json").content(cuerpo(codigoCorrecto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMIN"))
                .andExpect(jsonPath("$.dni").value(placa));
        Usuario creado = usuarioRepository.findByCorreo(correo).get(0);
        assertEquals("ADMIN", creado.getRol());
        assertEquals("Municipalidad de Lima", creado.getDistrito());
    }

    @Test
    void conUnCodigoIncorrectoNoSeCreaLaCuenta() throws Exception {
        mvc.perform(post("/api/usuarios/registrar-institucional").contentType("application/json").content(cuerpo("CODIGO-FALSO")))
                .andExpect(status().isForbidden());
        assertTrue(usuarioRepository.findByCorreo(correo).isEmpty());
    }

    @Test
    void faltandoCamposObligatoriosResponde400() throws Exception {
        mvc.perform(post("/api/usuarios/registrar-institucional").contentType("application/json")
                        .content("{\"nombres\":\"\",\"correo\":\"mal\"}"))
                .andExpect(status().isBadRequest());
    }
}
