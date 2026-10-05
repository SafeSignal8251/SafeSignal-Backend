package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;

public interface SuscripcionZonaService {

    List<SuscripcionZonaDTO> listar();

    SuscripcionZonaDTO obtenerPorId(Long id);

    // HU-36: datos trae etiqueta, punto, radio, categorías, canales y horario (todo opcional)
    SuscripcionZonaDTO registrar(Long id_usuario, Long id_zona, SuscripcionZonaDTO datos);

    SuscripcionZonaDTO actualizar(Long id, SuscripcionZonaDTO datos);

    List<SuscripcionZonaDTO> listarPorUsuario(Long id_usuario);

    List<SuscripcionZonaDTO> listarPorZona(Long id_zona);

    List<SuscripcionZonaDTO> listarPorUsuarioYZona(Long id_usuario, Long id_zona);

    void eliminar(Long id);
}
