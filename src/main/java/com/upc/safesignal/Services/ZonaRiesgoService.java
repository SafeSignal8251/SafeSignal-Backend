package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;

public interface ZonaRiesgoService {

    List<ZonaRiesgoDTO> listar();

    ZonaRiesgoDTO obtenerPorId(Long id);

    ZonaRiesgoDTO registrar(ZonaRiesgoDTO zonaDTO);

    ZonaRiesgoDTO actualizar(Long id, ZonaRiesgoDTO datos);

    ZonaRiesgoDTO configurarRiesgo(Long id, String nivel_riesgo, Double puntaje_riesgo);

    // HU-43: configurar umbrales de la zona
    ZonaRiesgoDTO configurarUmbrales(Long id, ZonaRiesgoDTO umbrales);

    ZonaRiesgoDTO recalcularRiesgo(Long id);

    List<ZonaRiesgoDTO> listarActivas();

    List<ZonaRiesgoDTO> listarPorEstado(Boolean estado);

    List<ZonaRiesgoDTO> listarPorNivel(String nivel);

    List<ZonaRiesgoDTO> buscarPorNombre(String nombre);

    ZonaRiesgoDTO cambiarEstado(Long id, Boolean estado);

    void eliminar(Long id);
}
