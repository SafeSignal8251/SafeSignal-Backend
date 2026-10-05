package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;

public interface ComentarioService {

    List<ComentarioDTO> listar();

    ComentarioDTO obtenerPorId(Long id);

    // HU-35: comentar en un reporte comunitario
    ComentarioDTO registrar(Long id_usuario, Long id_incidente, ComentarioDTO comentarioDTO);

    List<ComentarioDTO> listarPorUsuario(Long id_usuario);

    List<ComentarioDTO> listarPorIncidente(Long id_incidente);

    ComentarioDTO actualizar(Long id, String texto);

    void eliminar(Long id);
}
