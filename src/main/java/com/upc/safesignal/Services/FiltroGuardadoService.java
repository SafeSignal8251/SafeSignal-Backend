package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;

public interface FiltroGuardadoService {

    FiltroGuardadoDTO obtenerPorId(Long id);

    // HU-42 / HU-44: guardar filtro o vista favorita
    FiltroGuardadoDTO registrar(Long id_usuario, FiltroGuardadoDTO filtroDTO);

    List<FiltroGuardadoDTO> listarPorUsuario(Long id_usuario);

    void eliminar(Long id, Long id_usuario);
}
