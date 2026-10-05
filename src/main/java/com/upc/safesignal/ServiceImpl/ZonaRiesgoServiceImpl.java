package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.ZonaRiesgoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ZonaRiesgoServiceImpl implements ZonaRiesgoService {

    @Autowired
    private ZonaRiesgoRepository zonaRepository;

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ZonaRiesgoDTO> listar() {
        return aDTOs(zonaRepository.findAll());
    }

    @Override
    public ZonaRiesgoDTO obtenerPorId(Long id) {
        return aDTO(buscarZona(id));
    }

    @Override
    public ZonaRiesgoDTO registrar(ZonaRiesgoDTO zonaDTO) {
        if (zonaDTO.getNombre() == null || zonaDTO.getNombre().isBlank()) {
            throw new RuntimeException("El nombre de la zona es obligatorio");
        }
        if (zonaDTO.getLatitud() == null || zonaDTO.getLongitud() == null) {
            throw new RuntimeException("La latitud y la longitud son obligatorias");
        }
        // DTO -> entidad
        ZonaRiesgo zona = modelMapper.map(zonaDTO, ZonaRiesgo.class);
        zona.setId_zona(null);
        zona.setFecha_actualizacion(LocalDateTime.now());
        zona.setEstado(true);
        if (zona.getNivel_riesgo() == null) {
            zona.setNivel_riesgo("BAJO");
        }
        // entidad -> DTO
        return aDTO(zonaRepository.save(zona));
    }

    @Override
    public ZonaRiesgoDTO actualizar(Long id, ZonaRiesgoDTO datos) {
        ZonaRiesgo zona = buscarZona(id);
        if (datos.getNombre() != null) zona.setNombre(datos.getNombre());
        if (datos.getNivel_riesgo() != null) zona.setNivel_riesgo(datos.getNivel_riesgo());
        if (datos.getPuntaje_riesgo() != null) zona.setPuntaje_riesgo(datos.getPuntaje_riesgo());
        if (datos.getLatitud() != null) zona.setLatitud(datos.getLatitud());
        if (datos.getLongitud() != null) zona.setLongitud(datos.getLongitud());
        if (datos.getRadio_metros() != null) zona.setRadio_metros(datos.getRadio_metros());
        zona.setFecha_actualizacion(LocalDateTime.now());
        return aDTO(zonaRepository.save(zona));
    }

    @Override
    public ZonaRiesgoDTO configurarRiesgo(Long id, String nivel_riesgo, Double puntaje_riesgo) {
        ZonaRiesgo zona = buscarZona(id);
        zona.setNivel_riesgo(nivel_riesgo);
        zona.setPuntaje_riesgo(puntaje_riesgo);
        zona.setFecha_actualizacion(LocalDateTime.now());
        return aDTO(zonaRepository.save(zona));
    }

    // HU-43: configurar umbrales (incidentes por km²) y reclasificar la zona
    @Override
    public ZonaRiesgoDTO configurarUmbrales(Long id, ZonaRiesgoDTO umbrales) {
        Integer altoMin = umbrales.getUmbral_alto_min();
        Integer medioMin = umbrales.getUmbral_medio_min();
        Integer medioMax = umbrales.getUmbral_medio_max();
        Integer bajoMax = umbrales.getUmbral_bajo_max();

        if (altoMin == null || medioMin == null || medioMax == null || bajoMax == null) {
            throw new RuntimeException("Todos los umbrales son obligatorios");
        }
        if (!(bajoMax < medioMin && medioMin <= medioMax && medioMax < altoMin)) {
            throw new RuntimeException("Los umbrales deben cumplir: bajo máx < medio mín <= medio máx < alto mín");
        }
        if (umbrales.getVentana_dias() != null && umbrales.getVentana_dias() <= 0) {
            throw new RuntimeException("La ventana de análisis debe ser mayor a 0 días");
        }
        ZonaRiesgo zona = buscarZona(id);
        zona.setUmbral_alto_min(altoMin);
        zona.setUmbral_medio_min(medioMin);
        zona.setUmbral_medio_max(medioMax);
        zona.setUmbral_bajo_max(bajoMax);
        if (umbrales.getVentana_dias() != null) zona.setVentana_dias(umbrales.getVentana_dias());
        if (umbrales.getRadio_geocerca() != null) zona.setRadio_geocerca(umbrales.getRadio_geocerca());
        clasificar(zona);
        return aDTO(zonaRepository.save(zona));
    }

    @Override
    public ZonaRiesgoDTO recalcularRiesgo(Long id) {
        ZonaRiesgo zona = buscarZona(id);
        clasificar(zona);
        return aDTO(zonaRepository.save(zona));
    }

    @Override
    public List<ZonaRiesgoDTO> listarActivas() {
        return aDTOs(zonaRepository.findByEstado(true));
    }

    @Override
    public List<ZonaRiesgoDTO> listarPorEstado(Boolean estado) {
        return aDTOs(zonaRepository.findByEstado(estado));
    }

    @Override
    public List<ZonaRiesgoDTO> listarPorNivel(String nivel) {
        return aDTOs(zonaRepository.findByNivel(nivel.toUpperCase()));
    }

    @Override
    public List<ZonaRiesgoDTO> buscarPorNombre(String nombre) {
        return aDTOs(zonaRepository.findByNombreContainingIgnoreCase(nombre));
    }

    @Override
    public ZonaRiesgoDTO cambiarEstado(Long id, Boolean estado) {
        ZonaRiesgo zona = buscarZona(id);
        zona.setEstado(estado);
        return aDTO(zonaRepository.save(zona));
    }

    @Override
    public void eliminar(Long id) {
        zonaRepository.delete(buscarZona(id));
    }

    // ---------- métodos privados ----------

    private ZonaRiesgo buscarZona(Long id) {
        return zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada con id: " + id));
    }

    // entidad -> DTO
    private ZonaRiesgoDTO aDTO(ZonaRiesgo zona) {
        return modelMapper.map(zona, ZonaRiesgoDTO.class);
    }

    private List<ZonaRiesgoDTO> aDTOs(List<ZonaRiesgo> zonas) {
        List<ZonaRiesgoDTO> resultado = new ArrayList<>();
        for (ZonaRiesgo zona : zonas) {
            resultado.add(aDTO(zona));
        }
        return resultado;
    }

    // Nivel según la densidad de incidentes (por km²) dentro de la ventana de análisis
    private void clasificar(ZonaRiesgo zona) {
        int ventana = 30;
        if (zona.getVentana_dias() != null) {
            ventana = zona.getVentana_dias();
        }
        LocalDateTime desde = LocalDateTime.now().minusDays(ventana);

        int total = 0;
        for (Incidente i : incidenteRepository.findByZona(zona)) {
            if (i.getFecha_hora() != null && i.getFecha_hora().isAfter(desde)
                    && !"ELIMINADO".equals(i.getEstado())) {
                total++;
            }
        }

        double densidad = total;
        if (zona.getRadio_metros() != null && zona.getRadio_metros() > 0) {
            double radioKm = zona.getRadio_metros() / 1000.0;
            densidad = total / (Math.PI * radioKm * radioKm);
        }

        int alto = 15;
        if (zona.getUmbral_alto_min() != null) {
            alto = zona.getUmbral_alto_min();
        }
        int medio = 6;
        if (zona.getUmbral_medio_min() != null) {
            medio = zona.getUmbral_medio_min();
        }

        if (densidad >= alto) {
            zona.setNivel_riesgo("ALTO");
        } else if (densidad >= medio) {
            zona.setNivel_riesgo("MEDIO");
        } else {
            zona.setNivel_riesgo("BAJO");
        }
        zona.setFecha_actualizacion(LocalDateTime.now());
    }
}
