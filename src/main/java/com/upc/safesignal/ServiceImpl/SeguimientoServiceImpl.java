package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.SeguimientoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeguimientoServiceImpl implements SeguimientoService {

    @Autowired
    private SeguimientoRepository seguimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ZonaRiesgoRepository zonaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<SeguimientoDTO> listar() {
        return aDTOs(seguimientoRepository.findAll());
    }

    @Override
    public SeguimientoDTO obtenerPorId(Long id) {
        return aDTO(buscarSeguimiento(id));
    }

    // HU-10 / HU-27: iniciar seguimiento (solo uno activo por usuario)
    @Override
    public SeguimientoDTO iniciar(Long id_usuario, SeguimientoDTO seguimientoDTO) {
        Usuario usuario = buscarUsuario(id_usuario);

        if (!seguimientoRepository.findByUsuarioAndEstado(usuario, "ACTIVO").isEmpty()) {
            throw new RuntimeException("Ya tienes un seguimiento activo");
        }
        if (seguimientoDTO.getTiempo_monitoreo() != null && seguimientoDTO.getTiempo_monitoreo() <= 0) {
            throw new RuntimeException("El tiempo de monitoreo debe ser mayor a 0");
        }

        Seguimiento seguimiento = modelMapper.map(seguimientoDTO, Seguimiento.class);
        seguimiento.setId_seguimiento(null);
        seguimiento.setUsuario(usuario);
        seguimiento.setFecha_inicio(LocalDateTime.now());
        seguimiento.setFecha_fin(null);
        seguimiento.setEstado("ACTIVO");
        return aDTO(seguimientoRepository.save(seguimiento));
    }

    // HU-23: iniciar seguimiento automático al salir de una zona segura
    // (zona segura = zona de riesgo con nivel BAJO)
    @Override
    public SeguimientoDTO iniciarAutomatico(Long id_usuario, Double latitud, Double longitud) {
        List<ZonaRiesgo> seguras = zonaRepository.findByNivel("BAJO");
        if (seguras.isEmpty()) {
            throw new RuntimeException("No hay zonas seguras configuradas");
        }
        for (ZonaRiesgo zona : seguras) {
            if (zona.getLatitud() != null && zona.getLongitud() != null && zona.getRadio_metros() != null
                    && distanciaMetros(latitud, longitud, zona.getLatitud(), zona.getLongitud())
                    <= zona.getRadio_metros()) {
                throw new RuntimeException("El usuario sigue dentro de la zona segura: " + zona.getNombre());
            }
        }
        SeguimientoDTO seguimientoDTO = new SeguimientoDTO();
        seguimientoDTO.setDestino("Automático");
        seguimientoDTO.setTiempo_monitoreo(30);
        return iniciar(id_usuario, seguimientoDTO);
    }

    @Override
    public SeguimientoDTO finalizar(Long id) {
        Seguimiento seguimiento = buscarSeguimiento(id);
        if (!"ACTIVO".equals(seguimiento.getEstado())) {
            throw new RuntimeException("El seguimiento ya está finalizado");
        }
        seguimiento.setFecha_fin(LocalDateTime.now());
        seguimiento.setEstado("FINALIZADO");
        return aDTO(seguimientoRepository.save(seguimiento));
    }

    @Override
    public List<SeguimientoDTO> listarPorUsuario(Long id_usuario) {
        return aDTOs(seguimientoRepository.findByUsuario(buscarUsuario(id_usuario)));
    }

    @Override
    public List<SeguimientoDTO> listarActivosPorUsuario(Long id_usuario) {
        return aDTOs(seguimientoRepository.findByUsuarioAndEstado(buscarUsuario(id_usuario), "ACTIVO"));
    }

    @Override
    public List<SeguimientoDTO> listarPorEstado(String estado) {
        return aDTOs(seguimientoRepository.findByEstado(estado.toUpperCase()));
    }

    @Override
    public void eliminar(Long id) {
        seguimientoRepository.delete(buscarSeguimiento(id));
    }

    // ---------- métodos privados ----------

    private Seguimiento buscarSeguimiento(Long id) {
        return seguimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguimiento no encontrado con id: " + id));
    }

    private Usuario buscarUsuario(Long id_usuario) {
        return usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // distancia en metros entre dos puntos (fórmula de Haversine)
    private double distanciaMetros(double lat1, double lng1, double lat2, double lng2) {
        double radioTierra = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return radioTierra * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private SeguimientoDTO aDTO(Seguimiento seguimiento) {
        SeguimientoDTO dto = modelMapper.map(seguimiento, SeguimientoDTO.class);
        if (seguimiento.getUsuario() != null) {
            dto.setId_usuario(seguimiento.getUsuario().getId_usuario());
        }
        return dto;
    }

    private List<SeguimientoDTO> aDTOs(List<Seguimiento> seguimientos) {
        List<SeguimientoDTO> resultado = new ArrayList<>();
        for (Seguimiento seguimiento : seguimientos) {
            resultado.add(aDTO(seguimiento));
        }
        return resultado;
    }
}
