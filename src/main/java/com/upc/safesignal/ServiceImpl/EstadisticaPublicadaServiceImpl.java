package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.EstadisticaPublicadaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EstadisticaPublicadaServiceImpl implements EstadisticaPublicadaService {

    @Autowired
    private EstadisticaPublicadaRepository estadisticaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private ZonaRiesgoRepository zonaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<EstadisticaPublicadaDTO> listar() {
        return aDTOs(estadisticaRepository.listarRecientes());
    }

    @Override
    public EstadisticaPublicadaDTO obtenerPorId(Long id) {
        return aDTO(buscarEstadistica(id));
    }

    // HU-24: publicar estadística (solo administradores)
    @Override
    public EstadisticaPublicadaDTO publicar(Long id_usuario, EstadisticaPublicadaDTO estadisticaDTO) {
        Usuario admin = usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!"ADMIN".equalsIgnoreCase(admin.getRol())) {
            throw new RuntimeException("Solo un administrador puede publicar estadísticas");
        }
        if (estadisticaDTO.getTitulo() == null || estadisticaDTO.getTitulo().isBlank()) {
            throw new RuntimeException("El título es obligatorio");
        }

        EstadisticaPublicada estadistica = modelMapper.map(estadisticaDTO, EstadisticaPublicada.class);
        estadistica.setId_estadistica(null);
        estadistica.setUsuario(admin);
        estadistica.setCodigo("REP-" + LocalDateTime.now().getYear() + "-" + (estadisticaRepository.count() + 1));
        estadistica.setFecha_publicacion(LocalDateTime.now());
        return aDTO(estadisticaRepository.save(estadistica));
    }

    @Override
    public void eliminar(Long id) {
        estadisticaRepository.delete(buscarEstadistica(id));
    }

    // HU-14: dashboard del administrador (distrito es opcional)
    @Override
    public Map<String, Object> dashboard(Long id_usuario, String distrito) {
        Usuario admin = usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!"ADMIN".equalsIgnoreCase(admin.getRol())) {
            throw new RuntimeException("Solo un administrador puede ver el dashboard");
        }

        List<Incidente> incidentes;
        if (distrito == null || distrito.isBlank()) {
            incidentes = incidenteRepository.findAll();
        } else {
            incidentes = incidenteRepository.findByDistrito(distrito);
        }
        int pendientes = 0;
        int verificados = 0;
        for (Incidente i : incidentes) {
            if ("PENDIENTE".equals(i.getEstado())) pendientes++;
            if ("VERIFICADO".equals(i.getEstado())) verificados++;
        }

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("distrito", distrito);
        dashboard.put("total_usuarios", usuarioRepository.count());
        dashboard.put("total_incidentes", incidentes.size());
        dashboard.put("incidentes_pendientes", pendientes);
        dashboard.put("incidentes_verificados", verificados);
        dashboard.put("alertas_activas", alertaRepository.findByEstado("ACTIVA").size());
        dashboard.put("zonas_riesgo_alto", zonaRepository.findByNivel("ALTO").size());
        return dashboard;
    }

    // HU-41: exportar la estadística como CSV (total de incidentes por distrito y tipo, sin los ELIMINADO)
    @Override
    public String exportarCsv(Long id) {
        EstadisticaPublicada estadistica = buscarEstadistica(id);

        Map<String, Integer> totales = new LinkedHashMap<>();
        for (Incidente i : incidenteRepository.findAll()) {
            if (!"ELIMINADO".equals(i.getEstado())) {
                String clave = i.getDistrito() + "," + i.getTipo_incidente();
                if (totales.containsKey(clave)) {
                    totales.put(clave, totales.get(clave) + 1);
                } else {
                    totales.put(clave, 1);
                }
            }
        }

        StringBuilder csv = new StringBuilder();
        csv.append("codigo,").append(estadistica.getCodigo()).append("\n");
        csv.append("titulo,").append(estadistica.getTitulo()).append("\n");
        csv.append("distrito,tipo_incidente,total\n");
        for (String clave : totales.keySet()) {
            csv.append(clave).append(",").append(totales.get(clave)).append("\n");
        }
        return csv.toString();
    }

    // ---------- métodos privados ----------

    private EstadisticaPublicada buscarEstadistica(Long id) {
        return estadisticaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estadística no encontrada con id: " + id));
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private EstadisticaPublicadaDTO aDTO(EstadisticaPublicada estadistica) {
        EstadisticaPublicadaDTO dto = modelMapper.map(estadistica, EstadisticaPublicadaDTO.class);
        if (estadistica.getUsuario() != null) {
            dto.setId_usuario(estadistica.getUsuario().getId_usuario());
        }
        return dto;
    }

    private List<EstadisticaPublicadaDTO> aDTOs(List<EstadisticaPublicada> estadisticas) {
        List<EstadisticaPublicadaDTO> resultado = new ArrayList<>();
        for (EstadisticaPublicada estadistica : estadisticas) {
            resultado.add(aDTO(estadistica));
        }
        return resultado;
    }
}
