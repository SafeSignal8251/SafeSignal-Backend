package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;

public interface IncidenteService {

    List<IncidenteDTO> listar();

    IncidenteDTO obtenerPorId(Long id);

    // HU-16 / HU-22: registrar incidente
    IncidenteDTO registrar(Long id_usuario, Long id_zona, IncidenteDTO incidenteDTO);

    // HU-16: reporte de incidente post-emergencia a partir de una alerta propia
    IncidenteDTO registrarPostEmergencia(Long id_usuario, Long id_alerta, String descripcion);

    List<IncidenteDTO> listarPorUsuario(Long id_usuario);

    List<IncidenteDTO> listarPorZona(Long id_zona);

    List<IncidenteDTO> listarPorEstado(String estado);

    // HU-20: incidentes cercanos (radio en metros)
    List<IncidenteDTO> listarCercanos(double lat, double lng, double radioMetros);

    // HU-37: editar un reporte propio
    IncidenteDTO actualizarDescripcion(Long id, Long id_usuario, String descripcion);

    IncidenteDTO cambiarEstado(Long id, String estado);

    // HU-46: reportes con denuncias de falsa alarma pendientes de moderar
    List<IncidenteDTO> listarDenunciados();

    // HU-39 / HU-46: descartar (ELIMINADO) o mantener (VERIFICADO)
    IncidenteDTO moderar(Long id, boolean descartar);

    // HU-38: incidentes filtrados para las estadísticas (distrito, tipologia y dias son opcionales)
    List<IncidenteDTO> listarFiltrados(String distrito, String tipologia, Integer dias);

    void eliminar(Long id);
}
