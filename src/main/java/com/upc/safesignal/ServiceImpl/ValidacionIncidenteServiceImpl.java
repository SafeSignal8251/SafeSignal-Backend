package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.ValidacionIncidenteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValidacionIncidenteServiceImpl implements ValidacionIncidenteService {

    @Autowired
    private ValidacionIncidenteRepository validacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ValidacionIncidenteDTO> listar() {
        return aDTOs(validacionRepository.findAll());
    }

    @Override
    public ValidacionIncidenteDTO obtenerPorId(Long id) {
        return aDTO(buscarValidacion(id));
    }

    // HU-40: tipo_voto puede ser CONFIRMADO o FALSA_ALARMA
    @Override
    public ValidacionIncidenteDTO registrar(Long id_usuario, Long id_incidente, String tipo_voto) {
        String voto = "";
        if (tipo_voto != null) {
            voto = tipo_voto.trim().toUpperCase();
        }
        if (!voto.equals("CONFIRMADO") && !voto.equals("FALSA_ALARMA")) {
            throw new RuntimeException("tipo_voto debe ser CONFIRMADO o FALSA_ALARMA");
        }

        Usuario usuario = buscarUsuario(id_usuario);
        Incidente incidente = buscarIncidente(id_incidente);

        if (!validacionRepository.findByIncidenteAndUsuario(incidente, usuario).isEmpty()) {
            throw new RuntimeException("Ya registraste tu voto en este reporte");
        }

        ValidacionIncidente validacion = new ValidacionIncidente();
        validacion.setUsuario(usuario);
        validacion.setIncidente(incidente);
        validacion.setTipo_voto(voto);
        validacion.setFecha(LocalDateTime.now());
        return aDTO(validacionRepository.save(validacion));
    }

    @Override
    public List<ValidacionIncidenteDTO> listarPorIncidente(Long id_incidente) {
        return aDTOs(validacionRepository.findByIncidente(buscarIncidente(id_incidente)));
    }

    @Override
    public List<ValidacionIncidenteDTO> listarPorUsuario(Long id_usuario) {
        return aDTOs(validacionRepository.findByUsuario(buscarUsuario(id_usuario)));
    }

    @Override
    public long contarVotos(Long id_incidente, String tipo_voto) {
        return validacionRepository.contarVotos(buscarIncidente(id_incidente), tipo_voto.toUpperCase());
    }

    @Override
    public void eliminar(Long id) {
        validacionRepository.delete(buscarValidacion(id));
    }

    // ---------- métodos privados ----------

    private ValidacionIncidente buscarValidacion(Long id) {
        return validacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Validación no encontrada con id: " + id));
    }

    private Usuario buscarUsuario(Long id_usuario) {
        return usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private Incidente buscarIncidente(Long id_incidente) {
        return incidenteRepository.findById(id_incidente)
                .orElseThrow(() -> new RuntimeException("Incidente no encontrado"));
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private ValidacionIncidenteDTO aDTO(ValidacionIncidente validacion) {
        ValidacionIncidenteDTO dto = modelMapper.map(validacion, ValidacionIncidenteDTO.class);
        if (validacion.getIncidente() != null) {
            dto.setId_incidente(validacion.getIncidente().getId_incidente());
        }
        if (validacion.getUsuario() != null) {
            dto.setId_usuario(validacion.getUsuario().getId_usuario());
        }
        return dto;
    }

    private List<ValidacionIncidenteDTO> aDTOs(List<ValidacionIncidente> validaciones) {
        List<ValidacionIncidenteDTO> resultado = new ArrayList<>();
        for (ValidacionIncidente validacion : validaciones) {
            resultado.add(aDTO(validacion));
        }
        return resultado;
    }
}
