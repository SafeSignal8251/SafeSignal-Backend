package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;
import java.util.Map;

public interface EstadisticaPublicadaService {

    List<EstadisticaPublicadaDTO> listar();

    EstadisticaPublicadaDTO obtenerPorId(Long id);

    // HU-24: publicar estadística (solo administradores)
    EstadisticaPublicadaDTO publicar(Long id_usuario, EstadisticaPublicadaDTO estadisticaDTO);

    void eliminar(Long id);

    // HU-14: dashboard del administrador (distrito es opcional)
    Map<String, Object> dashboard(Long id_usuario, String distrito);

    // HU-41: exportar la estadística como CSV
    String exportarCsv(Long id);
}
