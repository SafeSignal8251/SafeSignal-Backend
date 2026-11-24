package com.upc.safesignal.service;

import com.upc.safesignal.Entities.ZonaRiesgo;
import com.upc.safesignal.Repositories.ZonaRiesgoRepository;
import com.upc.safesignal.ServiceImpl.ZonaResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZonaResolverTest {

    @Mock
    private ZonaRiesgoRepository zonaRepository;

    @InjectMocks
    private ZonaResolver resolver;

    private ZonaRiesgo zona(long id, String nombre, double lat, double lng) {
        ZonaRiesgo z = new ZonaRiesgo();
        z.setId_zona(id);
        z.setNombre(nombre);
        z.setLatitud(lat);
        z.setLongitud(lng);
        return z;
    }

    @Test
    void sinZonasCreaUnaConLosDatosDelReporte() {
        when(zonaRepository.findAll()).thenReturn(List.of());
        when(zonaRepository.save(any(ZonaRiesgo.class))).thenAnswer(i -> i.getArgument(0));

        ZonaRiesgo creada = resolver.masCercanaOCrear(-12.0464, -77.0428, "Lima Cercado", "ALTO");

        assertEquals("Lima Cercado", creada.getNombre());
        assertEquals("ALTO", creada.getNivel_riesgo());
        assertEquals(-12.0464, creada.getLatitud());
        assertEquals(500.0, creada.getRadio_metros());
        assertTrue(creada.getEstado());
        verify(zonaRepository).save(any(ZonaRiesgo.class));
    }

    @Test
    void sinNombreNiNivelUsaValoresPorDefecto() {
        when(zonaRepository.findAll()).thenReturn(List.of());
        when(zonaRepository.save(any(ZonaRiesgo.class))).thenAnswer(i -> i.getArgument(0));

        ZonaRiesgo creada = resolver.masCercanaOCrear(-12.0, -77.0, " ", null);

        assertEquals("Zona reportada", creada.getNombre());
        assertEquals("MEDIO", creada.getNivel_riesgo());
    }

    @Test
    void conZonasDevuelveLaMasCercanaYNoCreaNinguna() {
        ZonaRiesgo lejos = zona(1, "Callao", -12.05, -77.12);
        ZonaRiesgo cerca = zona(2, "Cercado", -12.046, -77.043);
        when(zonaRepository.findAll()).thenReturn(List.of(lejos, cerca));

        ZonaRiesgo elegida = resolver.masCercanaOCrear(-12.0464, -77.0428, "Lima", "MEDIO");

        assertEquals(2L, elegida.getId_zona());
        verify(zonaRepository, never()).save(any(ZonaRiesgo.class));
    }
}
