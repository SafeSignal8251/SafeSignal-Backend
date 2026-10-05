package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;

public interface ValidacionIncidenteService {

    List<ValidacionIncidenteDTO> listar();

    ValidacionIncidenteDTO obtenerPorId(Long id);

    // HU-40: tipo_voto puede ser CONFIRMADO o FALSA_ALARMA
    ValidacionIncidenteDTO registrar(Long id_usuario, Long id_incidente, String tipo_voto);

    List<ValidacionIncidenteDTO> listarPorIncidente(Long id_incidente);

    List<ValidacionIncidenteDTO> listarPorUsuario(Long id_usuario);

    long contarVotos(Long id_incidente, String tipo_voto);

    void eliminar(Long id);
}
