package com.upc.safesignal.service;

import com.upc.safesignal.Dtos.IncidenteDTO;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.ServiceImpl.IncidenteServiceImpl;
import com.upc.safesignal.Services.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// Pruebas unitarias de HU-16, HU-22 y HU-39/46 (incidentes y moderación)
@ExtendWith(MockitoExtension.class)
class IncidenteServiceImplTest {

    @Mock
    private IncidenteRepository incidenteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ZonaRiesgoRepository zonaRepository;
    @Mock
    private AlertaRepository alertaRepository;
    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private IncidenteServiceImpl incidenteService;

    private Usuario usuario;
    private ZonaRiesgo zona;

    @BeforeEach
    void preparar() {
        ReflectionTestUtils.setField(incidenteService, "modelMapper", new ModelMapper());
        usuario = new Usuario();
        usuario.setId_usuario(1L);
        usuario.setDistrito("Miraflores");
        zona = new ZonaRiesgo();
        zona.setId_zona(2L);
        zona.setNombre("Parque Kennedy");
    }

    private IncidenteDTO reporte() {
        IncidenteDTO dto = new IncidenteDTO();
        dto.setTipo_incidente("ROBO");
        dto.setLatitud(-12.12);
        dto.setLongitud(-77.03);
        return dto;
    }

    @Test
    void registrar_quedaPendiente_ynotificaALosSuscriptoresDeLaZona() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(zonaRepository.findById(2L)).thenReturn(Optional.of(zona));
        when(incidenteRepository.save(any(Incidente.class))).thenAnswer(inv -> inv.getArgument(0));

        IncidenteDTO resultado = incidenteService.registrar(1L, 2L, reporte());

        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("Miraflores", resultado.getDistrito(), "toma el distrito del usuario si no se envía");
        verify(notificacionService).notificarSuscriptores(eq(zona), anyString());
    }

    @Test
    void registrar_sinTipo_lanzaError() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(zonaRepository.findById(2L)).thenReturn(Optional.of(zona));
        IncidenteDTO dto = reporte();
        dto.setTipo_incidente(" ");

        assertThrows(RuntimeException.class, () -> incidenteService.registrar(1L, 2L, dto));
        verify(incidenteRepository, never()).save(any());
    }

    @Test
    void registrar_sinUbicacion_lanzaError() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(zonaRepository.findById(2L)).thenReturn(Optional.of(zona));
        IncidenteDTO dto = reporte();
        dto.setLatitud(null);

        assertThrows(RuntimeException.class, () -> incidenteService.registrar(1L, 2L, dto));
    }

    @Test
    void registrarPostEmergencia_sobreAlertaAjena_lanzaError() {
        Usuario otro = new Usuario();
        otro.setId_usuario(99L);
        Alerta alerta = new Alerta();
        alerta.setUsuario(otro);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(alertaRepository.findById(5L)).thenReturn(Optional.of(alerta));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> incidenteService.registrarPostEmergencia(1L, 5L, "Me siguieron"));

        assertTrue(ex.getMessage().contains("propias"));
        verify(incidenteRepository, never()).save(any());
    }

    @Test
    void moderar_descartar_marcaComoEliminado() {
        Incidente i = new Incidente();
        i.setId_incidente(3L);
        when(incidenteRepository.findById(3L)).thenReturn(Optional.of(i));
        when(incidenteRepository.save(any(Incidente.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals("ELIMINADO", incidenteService.moderar(3L, true).getEstado());
    }

    @Test
    void moderar_mantener_marcaComoVerificado() {
        Incidente i = new Incidente();
        i.setId_incidente(3L);
        when(incidenteRepository.findById(3L)).thenReturn(Optional.of(i));
        when(incidenteRepository.save(any(Incidente.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals("VERIFICADO", incidenteService.moderar(3L, false).getEstado());
    }

    @Test
    void obtenerPorId_inexistente_lanzaError() {
        when(incidenteRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> incidenteService.obtenerPorId(404L));
    }
}
