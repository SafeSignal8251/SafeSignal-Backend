package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.UbicacionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private SeguimientoRepository seguimientoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<UbicacionDTO> listar() {
        return aDTOs(ubicacionRepository.findAll());
    }

    @Override
    public UbicacionDTO obtenerPorId(Long id) {
        return aDTO(buscarUbicacion(id));
    }

    // HU-10: registrar un punto de la ubicación en tiempo real
    @Override
    public UbicacionDTO registrar(Long id_seguimiento, UbicacionDTO ubicacionDTO) {
        Seguimiento seguimiento = buscarSeguimiento(id_seguimiento);
        if (!"ACTIVO".equals(seguimiento.getEstado())) {
            throw new RuntimeException("El seguimiento no está activo");
        }
        if (ubicacionDTO.getLatitud() == null || ubicacionDTO.getLongitud() == null) {
            throw new RuntimeException("La latitud y la longitud son obligatorias");
        }

        Ubicacion ubicacion = modelMapper.map(ubicacionDTO, Ubicacion.class);
        ubicacion.setId_ubicacion(null);
        ubicacion.setSeguimiento(seguimiento);
        ubicacion.setFecha_hora(LocalDateTime.now());
        return aDTO(ubicacionRepository.save(ubicacion));
    }

    @Override
    public List<UbicacionDTO> listarPorSeguimiento(Long id_seguimiento) {
        return aDTOs(ubicacionRepository.findBySeguimiento(buscarSeguimiento(id_seguimiento)));
    }

    // HU-10: última ubicación compartida del seguimiento
    @Override
    public UbicacionDTO ultima(Long id_seguimiento) {
        List<Ubicacion> puntos = ubicacionRepository.ultimaPorSeguimiento(buscarSeguimiento(id_seguimiento));
        if (puntos.isEmpty()) {
            throw new RuntimeException("Aún no hay ubicaciones registradas");
        }
        return aDTO(puntos.get(0));
    }

    @Override
    public void eliminar(Long id) {
        ubicacionRepository.delete(buscarUbicacion(id));
    }

    // ---------- métodos privados ----------

    private Ubicacion buscarUbicacion(Long id) {
        return ubicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ubicación no encontrada con id: " + id));
    }

    private Seguimiento buscarSeguimiento(Long id_seguimiento) {
        return seguimientoRepository.findById(id_seguimiento)
                .orElseThrow(() -> new RuntimeException("Seguimiento no encontrado"));
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private UbicacionDTO aDTO(Ubicacion ubicacion) {
        UbicacionDTO dto = modelMapper.map(ubicacion, UbicacionDTO.class);
        if (ubicacion.getSeguimiento() != null) {
            dto.setId_seguimiento(ubicacion.getSeguimiento().getId_seguimiento());
        }
        return dto;
    }

    private List<UbicacionDTO> aDTOs(List<Ubicacion> ubicaciones) {
        List<UbicacionDTO> resultado = new ArrayList<>();
        for (Ubicacion ubicacion : ubicaciones) {
            resultado.add(aDTO(ubicacion));
        }
        return resultado;
    }
}
