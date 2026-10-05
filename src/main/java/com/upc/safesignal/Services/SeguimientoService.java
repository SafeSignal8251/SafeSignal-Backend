package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;

public interface SeguimientoService {

    List<SeguimientoDTO> listar();

    SeguimientoDTO obtenerPorId(Long id);

    // HU-10 / HU-27: iniciar seguimiento (solo uno activo por usuario)
    SeguimientoDTO iniciar(Long id_usuario, SeguimientoDTO seguimientoDTO);

    // HU-23: iniciar seguimiento automático al salir de una zona segura
    SeguimientoDTO iniciarAutomatico(Long id_usuario, Double latitud, Double longitud);

    SeguimientoDTO finalizar(Long id);

    List<SeguimientoDTO> listarPorUsuario(Long id_usuario);

    List<SeguimientoDTO> listarActivosPorUsuario(Long id_usuario);

    List<SeguimientoDTO> listarPorEstado(String estado);

    void eliminar(Long id);
}
