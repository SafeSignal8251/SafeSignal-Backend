package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.ComentarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComentarioServiceImpl implements ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ComentarioDTO> listar() {
        return aDTOs(comentarioRepository.findAll());
    }

    @Override
    public ComentarioDTO obtenerPorId(Long id) {
        return aDTO(buscarComentario(id));
    }

    // HU-35: comentar en un reporte comunitario
    @Override
    public ComentarioDTO registrar(Long id_usuario, Long id_incidente, ComentarioDTO comentarioDTO) {
        validarTexto(comentarioDTO.getTexto());
        Usuario usuario = buscarUsuario(id_usuario);
        Incidente incidente = buscarIncidente(id_incidente);

        Comentario comentario = modelMapper.map(comentarioDTO, Comentario.class);
        comentario.setId_comentario(null);
        comentario.setUsuario(usuario);
        comentario.setIncidente(incidente);
        comentario.setFecha_hora(LocalDateTime.now());
        return aDTO(comentarioRepository.save(comentario));
    }

    @Override
    public List<ComentarioDTO> listarPorUsuario(Long id_usuario) {
        return aDTOs(comentarioRepository.findByUsuario(buscarUsuario(id_usuario)));
    }

    @Override
    public List<ComentarioDTO> listarPorIncidente(Long id_incidente) {
        return aDTOs(comentarioRepository.findByIncidente(buscarIncidente(id_incidente)));
    }

    @Override
    public ComentarioDTO actualizar(Long id, String texto) {
        validarTexto(texto);
        Comentario comentario = buscarComentario(id);
        comentario.setTexto(texto);
        return aDTO(comentarioRepository.save(comentario));
    }

    @Override
    public void eliminar(Long id) {
        comentarioRepository.delete(buscarComentario(id));
    }

    // ---------- métodos privados ----------

    private Comentario buscarComentario(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con id: " + id));
    }

    private Usuario buscarUsuario(Long id_usuario) {
        return usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private Incidente buscarIncidente(Long id_incidente) {
        return incidenteRepository.findById(id_incidente)
                .orElseThrow(() -> new RuntimeException("Incidente no encontrado"));
    }

    private void validarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new RuntimeException("El comentario no puede estar vacío");
        }
        if (texto.length() > 500) {
            throw new RuntimeException("El comentario no puede superar los 500 caracteres");
        }
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private ComentarioDTO aDTO(Comentario comentario) {
        ComentarioDTO dto = modelMapper.map(comentario, ComentarioDTO.class);
        if (comentario.getIncidente() != null) {
            dto.setId_incidente(comentario.getIncidente().getId_incidente());
        }
        if (comentario.getUsuario() != null) {
            dto.setId_usuario(comentario.getUsuario().getId_usuario());
        }
        return dto;
    }

    private List<ComentarioDTO> aDTOs(List<Comentario> comentarios) {
        List<ComentarioDTO> resultado = new ArrayList<>();
        for (Comentario comentario : comentarios) {
            resultado.add(aDTO(comentario));
        }
        return resultado;
    }
}
