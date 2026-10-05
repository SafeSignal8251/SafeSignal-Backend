package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class IncidenteServiceImpl implements IncidenteService {

    private static final int UMBRAL_DENUNCIAS = 1;

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ZonaRiesgoRepository zonaRepository;

    @Autowired
    private ValidacionIncidenteRepository validacionRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<IncidenteDTO> listar() {
        return aDTOs(incidenteRepository.findAll());
    }

    @Override
    public IncidenteDTO obtenerPorId(Long id) {
        return aDTO(buscarIncidente(id));
    }

    // HU-16 / HU-22: registrar incidente
    @Override
    public IncidenteDTO registrar(Long id_usuario, Long id_zona, IncidenteDTO incidenteDTO) {
        Usuario usuario = usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        ZonaRiesgo zona = zonaRepository.findById(id_zona)
                .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada"));

        if (incidenteDTO.getTipo_incidente() == null || incidenteDTO.getTipo_incidente().isBlank()) {
            throw new RuntimeException("El tipo de incidente es obligatorio");
        }
        if (incidenteDTO.getLatitud() == null || incidenteDTO.getLongitud() == null) {
            throw new RuntimeException("La ubicación del incidente es obligatoria");
        }

        Incidente incidente = modelMapper.map(incidenteDTO, Incidente.class);
        incidente.setId_incidente(null);
        incidente.setUsuario(usuario);
        incidente.setZona(zona);
        incidente.setFecha_hora(LocalDateTime.now());
        incidente.setEstado("PENDIENTE");
        if (incidente.getDistrito() == null) {
            incidente.setDistrito(usuario.getDistrito());
        }

        Incidente guardado = incidenteRepository.save(incidente);
        log.info("Incidente registrado con id: {}", guardado.getId_incidente());

        // HU-36: avisar a los suscriptores de la zona
        notificacionService.notificarSuscriptores(zona,
                "Nuevo reporte en " + zona.getNombre() + ": " + guardado.getTipo_incidente());

        return aDTO(guardado);
    }

    // HU-16: reporte post-emergencia a partir de una alerta propia
    @Override
    public IncidenteDTO registrarPostEmergencia(Long id_usuario, Long id_alerta, String descripcion) {
        Usuario usuario = usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Alerta alerta = alertaRepository.findById(id_alerta)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        if (!alerta.getUsuario().getId_usuario().equals(id_usuario)) {
            throw new RuntimeException("Solo puedes reportar sobre tus propias alertas");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new RuntimeException("La descripción es obligatoria");
        }

        Incidente incidente = new Incidente();
        incidente.setTipo_incidente("POST_EMERGENCIA");
        incidente.setDescripcion(descripcion);
        incidente.setLatitud(alerta.getLatitud());
        incidente.setLongitud(alerta.getLongitud());
        incidente.setDireccion(alerta.getDireccion());
        incidente.setDistrito(usuario.getDistrito());
        incidente.setUsuario(usuario);
        incidente.setFecha_hora(LocalDateTime.now());
        incidente.setEstado("PENDIENTE");

        Incidente guardado = incidenteRepository.save(incidente);
        log.info("Reporte post-emergencia registrado con id: {}", guardado.getId_incidente());
        return aDTO(guardado);
    }

    @Override
    public List<IncidenteDTO> listarPorUsuario(Long id_usuario) {
        Usuario usuario = usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return aDTOs(incidenteRepository.findByUsuario(usuario));
    }

    @Override
    public List<IncidenteDTO> listarPorZona(Long id_zona) {
        ZonaRiesgo zona = zonaRepository.findById(id_zona)
                .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada"));
        return aDTOs(incidenteRepository.findByZona(zona));
    }

    @Override
    public List<IncidenteDTO> listarPorEstado(String estado) {
        return aDTOs(incidenteRepository.findByEstado(estado.toUpperCase()));
    }

    // HU-20: incidentes cercanos (radio en metros)
    @Override
    public List<IncidenteDTO> listarCercanos(double lat, double lng, double radioMetros) {
        double deltaLat = radioMetros / 111320.0;
        double deltaLng = radioMetros / (111320.0 * Math.cos(Math.toRadians(lat)));
        return aDTOs(incidenteRepository.findEnCuadro(
                lat - deltaLat, lat + deltaLat,
                lng - deltaLng, lng + deltaLng));
    }

    // HU-37: editar un reporte propio
    @Override
    public IncidenteDTO actualizarDescripcion(Long id, Long id_usuario, String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new RuntimeException("La descripción no puede estar vacía");
        }
        Incidente incidente = buscarIncidente(id);
        if (!incidente.getUsuario().getId_usuario().equals(id_usuario)) {
            throw new RuntimeException("Solo puedes editar tus propios reportes");
        }
        incidente.setDescripcion(descripcion);
        return aDTO(incidenteRepository.save(incidente));
    }

    @Override
    public IncidenteDTO cambiarEstado(Long id, String estado) {
        Incidente incidente = buscarIncidente(id);
        incidente.setEstado(estado.toUpperCase());
        return aDTO(incidenteRepository.save(incidente));
    }

    // HU-46: reportes con denuncias de falsa alarma pendientes de moderar
    @Override
    public List<IncidenteDTO> listarDenunciados() {
        List<Incidente> resultado = new ArrayList<>();
        for (Incidente i : incidenteRepository.findAll()) {
            if (!"ELIMINADO".equals(i.getEstado())
                    && validacionRepository.contarVotos(i, "FALSA_ALARMA") >= UMBRAL_DENUNCIAS) {
                resultado.add(i);
            }
        }
        return aDTOs(resultado);
    }

    // HU-39 / HU-46: descartar (ELIMINADO) o mantener (VERIFICADO)
    @Override
    public IncidenteDTO moderar(Long id, boolean descartar) {
        Incidente incidente = buscarIncidente(id);
        if (descartar) {
            incidente.setEstado("ELIMINADO");
        } else {
            incidente.setEstado("VERIFICADO");
        }
        log.info("Incidente {} moderado, nuevo estado: {}", id, incidente.getEstado());
        return aDTO(incidenteRepository.save(incidente));
    }

    // HU-38: incidentes filtrados para las estadísticas (distrito, tipologia y dias son opcionales)
    @Override
    public List<IncidenteDTO> listarFiltrados(String distrito, String tipologia, Integer dias) {
        LocalDateTime desde = null;
        if (dias != null) {
            desde = LocalDateTime.now().minusDays(dias);
        }

        List<Incidente> resultado = new ArrayList<>();
        for (Incidente i : incidenteRepository.findAll()) {
            if ("ELIMINADO".equals(i.getEstado())) continue;
            if (desde != null && (i.getFecha_hora() == null || !i.getFecha_hora().isAfter(desde))) continue;
            if (!coincide(distrito, i.getDistrito())) continue;
            if (!coincide(tipologia, i.getTipo_incidente())) continue;
            resultado.add(i);
        }
        return aDTOs(resultado);
    }

    @Override
    public void eliminar(Long id) {
        log.warn("Eliminando incidente con id: {}", id);
        incidenteRepository.delete(buscarIncidente(id));
    }

    // ---------- métodos privados ----------

    private Incidente buscarIncidente(Long id) {
        return incidenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidente no encontrado con id: " + id));
    }

    private boolean coincide(String filtro, String valor) {
        return filtro == null || filtro.isBlank() || "todos".equalsIgnoreCase(filtro)
                || filtro.equalsIgnoreCase(valor);
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private IncidenteDTO aDTO(Incidente incidente) {
        IncidenteDTO dto = modelMapper.map(incidente, IncidenteDTO.class);
        if (incidente.getZona() != null) {
            dto.setId_zona(incidente.getZona().getId_zona());
        }
        if (incidente.getUsuario() != null) {
            dto.setId_usuario(incidente.getUsuario().getId_usuario());
        }
        return dto;
    }

    private List<IncidenteDTO> aDTOs(List<Incidente> incidentes) {
        List<IncidenteDTO> resultado = new ArrayList<>();
        for (Incidente incidente : incidentes) {
            resultado.add(aDTO(incidente));
        }
        return resultado;
    }
}
