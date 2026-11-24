package com.upc.safesignal.service;

import com.upc.safesignal.Entities.Incidente;
import com.upc.safesignal.Entities.Usuario;
import com.upc.safesignal.Entities.ValidacionIncidente;
import com.upc.safesignal.Repositories.IncidenteRepository;
import com.upc.safesignal.Repositories.UsuarioRepository;
import com.upc.safesignal.Repositories.ValidacionIncidenteRepository;
import com.upc.safesignal.ServiceImpl.ValidacionIncidenteServiceImpl;
import com.upc.safesignal.Services.NotificacionService;
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

// Pruebas unitarias de HU-40 (validar o denunciar un reporte de la comunidad)
@ExtendWith(MockitoExtension.class)
class ValidacionIncidenteServiceImplTest {

    @Mock
    private ValidacionIncidenteRepository validacionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private IncidenteRepository incidenteRepository;
    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private ValidacionIncidenteServiceImpl validacionService;

    private Usuario autor;
    private Usuario vecino;
    private Incidente incidente;

    @BeforeEach
    void preparar() {
        ReflectionTestUtils.setField(validacionService, "modelMapper", new ModelMapper());
        autor = new Usuario();
        autor.setId_usuario(1L);
        vecino = new Usuario();
        vecino.setId_usuario(2L);
        incidente = new Incidente();
        incidente.setId_incidente(10L);
        incidente.setUsuario(autor);
    }

    @Test
    void elAutorNoPuedeValidarSuPropioReporte() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(incidenteRepository.findById(10L)).thenReturn(Optional.of(incidente));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> validacionService.registrar(1L, 10L, "CONFIRMADO"));

        assertTrue(ex.getMessage().contains("propio reporte"));
        verify(validacionRepository, never()).save(any());
    }

    @Test
    void elAutorNoPuedeDenunciarSuPropioReporteComoFalsaAlarma() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(incidenteRepository.findById(10L)).thenReturn(Optional.of(incidente));

        assertThrows(RuntimeException.class, () -> validacionService.registrar(1L, 10L, "FALSA_ALARMA"));
        verify(validacionRepository, never()).save(any());
    }

    @Test
    void otroVecinoSiPuedeValidarElReporte() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(vecino));
        when(incidenteRepository.findById(10L)).thenReturn(Optional.of(incidente));
        when(validacionRepository.findByIncidenteAndUsuario(incidente, vecino)).thenReturn(List.of());
        when(validacionRepository.save(any(ValidacionIncidente.class))).thenAnswer(inv -> inv.getArgument(0));

        var resultado = validacionService.registrar(2L, 10L, "confirmado");

        assertEquals("CONFIRMADO", resultado.getTipo_voto());
        verify(validacionRepository).save(any(ValidacionIncidente.class));
    }

    @Test
    void unaDenunciaDeFalsaAlarmaNotificaAlAutorYAlAdministrador() {
        Usuario admin = new Usuario();
        admin.setId_usuario(50L);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(vecino));
        when(incidenteRepository.findById(10L)).thenReturn(Optional.of(incidente));
        when(validacionRepository.findByIncidenteAndUsuario(incidente, vecino)).thenReturn(List.of());
        when(validacionRepository.save(any(ValidacionIncidente.class))).thenAnswer(inv -> inv.getArgument(0));
        when(validacionRepository.contarVotos(incidente, "FALSA_ALARMA")).thenReturn(1L);
        when(usuarioRepository.findByRol("ADMIN")).thenReturn(List.of(admin));

        validacionService.registrar(2L, 10L, "FALSA_ALARMA");

        verify(notificacionService).crearParaUsuario(eq(autor), contains("inconsistente"), eq("FALSA_ALARMA"), eq("MEDIA"));
        verify(notificacionService).crearParaUsuario(eq(admin), contains("denunciado como falsa alarma"), eq("FALSA_ALARMA"), eq("MEDIA"));
    }

    @Test
    void unaConfirmacionNoGeneraNotificaciones() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(vecino));
        when(incidenteRepository.findById(10L)).thenReturn(Optional.of(incidente));
        when(validacionRepository.findByIncidenteAndUsuario(incidente, vecino)).thenReturn(List.of());
        when(validacionRepository.save(any(ValidacionIncidente.class))).thenAnswer(inv -> inv.getArgument(0));

        validacionService.registrar(2L, 10L, "CONFIRMADO");

        verifyNoInteractions(notificacionService);
    }

    @Test
    void unVecinoNoPuedeVotarDosVecesElMismoReporte() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(vecino));
        when(incidenteRepository.findById(10L)).thenReturn(Optional.of(incidente));
        when(validacionRepository.findByIncidenteAndUsuario(incidente, vecino)).thenReturn(List.of(new ValidacionIncidente()));

        assertThrows(RuntimeException.class, () -> validacionService.registrar(2L, 10L, "CONFIRMADO"));
    }
}
