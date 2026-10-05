package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.FiltroGuardadoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FiltroGuardadoServiceImpl implements FiltroGuardadoService {

    @Autowired
    private FiltroGuardadoRepository filtroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public FiltroGuardadoDTO obtenerPorId(Long id) {
        return aDTO(buscarFiltro(id));
    }

    // HU-42 / HU-44: guardar filtro o vista favorita
    @Override
    public FiltroGuardadoDTO registrar(Long id_usuario, FiltroGuardadoDTO filtroDTO) {
        Usuario usuario = buscarUsuario(id_usuario);

        if (filtroDTO.getNombre() == null || filtroDTO.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del filtro es obligatorio");
        }
        for (FiltroGuardado f : filtroRepository.findByUsuario(usuario)) {
            if (f.getNombre().equalsIgnoreCase(filtroDTO.getNombre())) {
                throw new RuntimeException("Ya tienes un filtro con ese nombre");
            }
        }

        FiltroGuardado filtro = modelMapper.map(filtroDTO, FiltroGuardado.class);
        filtro.setId_filtro(null);
        filtro.setUsuario(usuario);
        filtro.setFecha_creacion(LocalDateTime.now());
        if (filtro.getRango_dias() == null) {
            filtro.setRango_dias(30);
        }
        return aDTO(filtroRepository.save(filtro));
    }

    @Override
    public List<FiltroGuardadoDTO> listarPorUsuario(Long id_usuario) {
        return aDTOs(filtroRepository.findByUsuario(buscarUsuario(id_usuario)));
    }

    @Override
    public void eliminar(Long id, Long id_usuario) {
        FiltroGuardado filtro = buscarFiltro(id);
        if (!filtro.getUsuario().getId_usuario().equals(id_usuario)) {
            throw new RuntimeException("Solo puedes eliminar tus propios filtros");
        }
        filtroRepository.delete(filtro);
    }

    // ---------- métodos privados ----------

    private FiltroGuardado buscarFiltro(Long id) {
        return filtroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filtro no encontrado con id: " + id));
    }

    private Usuario buscarUsuario(Long id_usuario) {
        return usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private FiltroGuardadoDTO aDTO(FiltroGuardado filtro) {
        FiltroGuardadoDTO dto = modelMapper.map(filtro, FiltroGuardadoDTO.class);
        if (filtro.getUsuario() != null) {
            dto.setId_usuario(filtro.getUsuario().getId_usuario());
        }
        return dto;
    }

    private List<FiltroGuardadoDTO> aDTOs(List<FiltroGuardado> filtros) {
        List<FiltroGuardadoDTO> resultado = new ArrayList<>();
        for (FiltroGuardado filtro : filtros) {
            resultado.add(aDTO(filtro));
        }
        return resultado;
    }
}
