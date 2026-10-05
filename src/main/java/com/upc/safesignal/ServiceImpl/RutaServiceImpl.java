package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.RutaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RutaServiceImpl implements RutaService {

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<RutaDTO> listar() {
        return aDTOs(rutaRepository.findAll());
    }

    @Override
    public RutaDTO obtenerPorId(Long id) {
        return aDTO(buscarRuta(id));
    }

    // HU-21: guardar ruta frecuente
    @Override
    public RutaDTO registrar(Long id_usuario, RutaDTO rutaDTO) {
        Usuario usuario = buscarUsuario(id_usuario);

        if (rutaDTO.getOrigen() == null || rutaDTO.getOrigen().isBlank()
                || rutaDTO.getDestino() == null || rutaDTO.getDestino().isBlank()) {
            throw new RuntimeException("El origen y el destino son obligatorios");
        }

        Ruta ruta = modelMapper.map(rutaDTO, Ruta.class);
        ruta.setId_ruta(null);
        ruta.setUsuario(usuario);
        ruta.setFecha_creacion(LocalDateTime.now());
        return aDTO(rutaRepository.save(ruta));
    }

    @Override
    public List<RutaDTO> listarPorUsuario(Long id_usuario) {
        return aDTOs(rutaRepository.findByUsuario(buscarUsuario(id_usuario)));
    }

    @Override
    public RutaDTO actualizar(Long id, RutaDTO datos) {
        Ruta ruta = buscarRuta(id);
        if (datos.getNombre() != null) ruta.setNombre(datos.getNombre());
        if (datos.getOrigen() != null) ruta.setOrigen(datos.getOrigen());
        if (datos.getDestino() != null) ruta.setDestino(datos.getDestino());
        if (datos.getOrigen_latitud() != null) ruta.setOrigen_latitud(datos.getOrigen_latitud());
        if (datos.getOrigen_longitud() != null) ruta.setOrigen_longitud(datos.getOrigen_longitud());
        if (datos.getDestino_latitud() != null) ruta.setDestino_latitud(datos.getDestino_latitud());
        if (datos.getDestino_longitud() != null) ruta.setDestino_longitud(datos.getDestino_longitud());
        if (datos.getDistancia() != null) ruta.setDistancia(datos.getDistancia());
        if (datos.getTiempo_estimado() != null) ruta.setTiempo_estimado(datos.getTiempo_estimado());
        if (datos.getTipo_ruta() != null) ruta.setTipo_ruta(datos.getTipo_ruta());
        return aDTO(rutaRepository.save(ruta));
    }

    @Override
    public void eliminar(Long id) {
        rutaRepository.delete(buscarRuta(id));
    }

    // HU-17: riesgo de una ruta según los incidentes cercanos
    @Override
    public Map<String, Object> evaluarRiesgo(Long id) {
        Ruta ruta = buscarRuta(id);
        if (!tieneCoordenadas(ruta)) {
            throw new RuntimeException("La ruta no tiene coordenadas de origen y destino");
        }
        int total = contarIncidentes(ruta);
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("id_ruta", ruta.getId_ruta());
        resultado.put("nombre", ruta.getNombre());
        resultado.put("incidentes_cercanos", total);
        resultado.put("nivel_riesgo", nivelRiesgo(total));
        return resultado;
    }

    // HU-17: rutas del usuario con nivel de riesgo BAJO
    @Override
    public List<RutaDTO> listarSeguras(Long id_usuario) {
        List<Ruta> seguras = new ArrayList<>();
        for (Ruta ruta : rutaRepository.findByUsuario(buscarUsuario(id_usuario))) {
            if (tieneCoordenadas(ruta) && "BAJO".equals(nivelRiesgo(contarIncidentes(ruta)))) {
                seguras.add(ruta);
            }
        }
        return aDTOs(seguras);
    }

    // HU-19: ruta alternativa con el mismo destino y menos incidentes
    @Override
    public RutaDTO sugerirAlternativa(Long id) {
        Ruta actual = buscarRuta(id);
        if (!tieneCoordenadas(actual)) {
            throw new RuntimeException("La ruta no tiene coordenadas de origen y destino");
        }
        int totalActual = contarIncidentes(actual);
        if ("BAJO".equals(nivelRiesgo(totalActual))) {
            throw new RuntimeException("La ruta actual ya es segura, no necesita alternativa");
        }

        Ruta mejor = null;
        int totalMejor = totalActual;
        for (Ruta otra : rutaRepository.findByUsuario(actual.getUsuario())) {
            if (!otra.getId_ruta().equals(id) && tieneCoordenadas(otra)
                    && otra.getDestino().equalsIgnoreCase(actual.getDestino())) {
                int total = contarIncidentes(otra);
                if (total < totalMejor) {
                    mejor = otra;
                    totalMejor = total;
                }
            }
        }
        if (mejor == null) {
            throw new RuntimeException("No hay una ruta alternativa más segura para ese destino");
        }
        return aDTO(mejor);
    }

    // ---------- métodos privados ----------

    private Ruta buscarRuta(Long id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con id: " + id));
    }

    private Usuario buscarUsuario(Long id_usuario) {
        return usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private boolean tieneCoordenadas(Ruta ruta) {
        return ruta.getOrigen_latitud() != null && ruta.getOrigen_longitud() != null
                && ruta.getDestino_latitud() != null && ruta.getDestino_longitud() != null;
    }

    // incidentes dentro del cuadro que encierra origen y destino (+ margen de unos 300 m)
    private int contarIncidentes(Ruta ruta) {
        double margen = 0.003;
        double latMin = Math.min(ruta.getOrigen_latitud(), ruta.getDestino_latitud()) - margen;
        double latMax = Math.max(ruta.getOrigen_latitud(), ruta.getDestino_latitud()) + margen;
        double lngMin = Math.min(ruta.getOrigen_longitud(), ruta.getDestino_longitud()) - margen;
        double lngMax = Math.max(ruta.getOrigen_longitud(), ruta.getDestino_longitud()) + margen;
        return incidenteRepository.findEnCuadro(latMin, latMax, lngMin, lngMax).size();
    }

    private String nivelRiesgo(int total) {
        if (total >= 15) return "ALTO";
        if (total >= 6) return "MEDIO";
        return "BAJO";
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private RutaDTO aDTO(Ruta ruta) {
        RutaDTO dto = modelMapper.map(ruta, RutaDTO.class);
        if (ruta.getUsuario() != null) {
            dto.setId_usuario(ruta.getUsuario().getId_usuario());
        }
        return dto;
    }

    private List<RutaDTO> aDTOs(List<Ruta> rutas) {
        List<RutaDTO> resultado = new ArrayList<>();
        for (Ruta ruta : rutas) {
            resultado.add(aDTO(ruta));
        }
        return resultado;
    }
}
