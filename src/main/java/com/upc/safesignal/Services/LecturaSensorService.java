package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;

public interface LecturaSensorService {

    List<LecturaSensorDTO> listar();

    LecturaSensorDTO obtenerPorId(Long id);

    // Registra la lectura y, si es anómala, genera una alerta automática (HU-15)
    LecturaSensorDTO registrar(Long id_dispositivo, LecturaSensorDTO lecturaDTO);

    List<LecturaSensorDTO> listarPorDispositivo(Long id_dispositivo);

    List<LecturaSensorDTO> listarAnomalias();

    List<LecturaSensorDTO> listarAnomaliasPorDispositivo(Long id_dispositivo);

    void eliminar(Long id);
}
