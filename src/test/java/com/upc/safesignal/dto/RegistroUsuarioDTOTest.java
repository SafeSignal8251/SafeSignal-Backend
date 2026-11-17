package com.upc.safesignal.dto;

import com.upc.safesignal.Dtos.RegistroUsuarioDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas de Bean Validation sobre el registro de cuenta (HU-01)
class RegistroUsuarioDTOTest {

    private static Validator validator;

    @BeforeAll
    static void crearValidador() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private RegistroUsuarioDTO valido() {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setNombres("Ana");
        dto.setApellidos("Perez");
        dto.setCorreo("ana@correo.com");
        dto.setContrasena("Clave12345");
        dto.setDni("12345678");
        return dto;
    }

    @Test
    void unRegistroCompletoEsValido() {
        assertTrue(validator.validate(valido()).isEmpty());
    }

    @Test
    void rechazaCorreoSinFormato() {
        RegistroUsuarioDTO dto = valido();
        dto.setCorreo("no-es-correo");

        assertEquals(1, validator.validate(dto).size());
    }

    @Test
    void rechazaContrasenaCorta() {
        RegistroUsuarioDTO dto = valido();
        dto.setContrasena("corta");

        assertEquals(1, validator.validate(dto).size());
    }

    @Test
    void rechazaDniConSimbolos() {
        RegistroUsuarioDTO dto = valido();
        dto.setDni("12-34");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void rechazaCamposObligatoriosVacios() {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();

        // nombres, apellidos, correo, contrasena y dni
        assertEquals(5, validator.validate(dto).stream().map(v -> v.getPropertyPath().toString()).distinct().count());
    }
}
