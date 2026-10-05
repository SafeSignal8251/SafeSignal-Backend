package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;
import java.util.Map;

public interface RutaService {

    List<RutaDTO> listar();

    RutaDTO obtenerPorId(Long id);

    // HU-21: guardar ruta frecuente
    RutaDTO registrar(Long id_usuario, RutaDTO rutaDTO);

    List<RutaDTO> listarPorUsuario(Long id_usuario);

    RutaDTO actualizar(Long id, RutaDTO datos);

    void eliminar(Long id);

    // HU-17: riesgo de una ruta según los incidentes cercanos
    Map<String, Object> evaluarRiesgo(Long id);

    // HU-17: rutas del usuario con nivel de riesgo BAJO
    List<RutaDTO> listarSeguras(Long id_usuario);

    // HU-19: ruta alternativa con el mismo destino y menos incidentes
    RutaDTO sugerirAlternativa(Long id);
}
