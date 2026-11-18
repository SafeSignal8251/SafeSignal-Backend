package com.upc.safesignal.service;

import com.upc.safesignal.Dtos.ContactoConfianzaDTO;
import com.upc.safesignal.Entities.ContactoConfianza;
import com.upc.safesignal.Entities.Usuario;
import com.upc.safesignal.Repositories.ContactoConfianzaRepository;
import com.upc.safesignal.Repositories.UsuarioRepository;
import com.upc.safesignal.ServiceImpl.ContactoConfianzaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Pruebas unitarias de HU-06 (contactos de confianza)
@ExtendWith(MockitoExtension.class)
class ContactoConfianzaServiceImplTest {

    @Mock
    private ContactoConfianzaRepository contactoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ContactoConfianzaServiceImpl contactoService;

    private Usuario usuario;

    @BeforeEach
    void preparar() {
        ReflectionTestUtils.setField(contactoService, "modelMapper", new ModelMapper());
        usuario = new Usuario();
        usuario.setId_usuario(1L);
    }

    private ContactoConfianzaDTO contacto(String nombres, String telefono) {
        ContactoConfianzaDTO dto = new ContactoConfianzaDTO();
        dto.setNombres(nombres);
        dto.setTelefono(telefono);
        return dto;
    }

    @Test
    void registrar_asignaValoresPorDefecto() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(contactoRepository.findByUsuario(usuario)).thenReturn(List.of());
        when(contactoRepository.save(any(ContactoConfianza.class))).thenAnswer(inv -> inv.getArgument(0));

        ContactoConfianzaDTO resultado = contactoService.registrar(1L, contacto("Maria", "999111222"));

        assertEquals(1, resultado.getPrioridad());
        assertTrue(resultado.getNotificar_sms());
        assertFalse(resultado.getNotificar_whatsapp());
        assertEquals(1L, resultado.getId_usuario());
    }

    @Test
    void registrar_conTelefonoRepetido_lanzaError() {
        ContactoConfianza existente = new ContactoConfianza();
        existente.setTelefono("999111222");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(contactoRepository.findByUsuario(usuario)).thenReturn(List.of(existente));

        assertThrows(RuntimeException.class, () -> contactoService.registrar(1L, contacto("Maria", "999111222")));
        verify(contactoRepository, never()).save(any());
    }

    @Test
    void registrar_sinNombreOTelefono_lanzaError() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(RuntimeException.class, () -> contactoService.registrar(1L, contacto("", "999111222")));
        assertThrows(RuntimeException.class, () -> contactoService.registrar(1L, contacto("Maria", null)));
    }

    @Test
    void registrar_conUsuarioInexistente_lanzaError() {
        when(usuarioRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> contactoService.registrar(5L, contacto("Maria", "999111222")));
    }

    @Test
    void eliminar_conContactoInexistente_lanzaError() {
        when(contactoRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> contactoService.eliminar(9L));
        verify(contactoRepository, never()).delete(any());
    }
}
