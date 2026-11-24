package com.upc.safesignal.ServiceImpl;

import org.springframework.transaction.annotation.Transactional;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.NotificacionService;
import com.upc.safesignal.Services.ValidacionIncidenteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ValidacionIncidenteServiceImpl implements ValidacionIncidenteService {

    @Autowired
    private ValidacionIncidenteRepository validacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private NotificacionService notificacionService;

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

        // El autor no puede validar ni denunciar su propio reporte
        if (incidente.getUsuario() != null && id_usuario.equals(incidente.getUsuario().getId_usuario())) {
            throw new RuntimeException("No puedes validar tu propio reporte");
        }

        if (!validacionRepository.findByIncidenteAndUsuario(incidente, usuario).isEmpty()) {
            throw new RuntimeException("Ya registraste tu voto en este reporte");
        }

        ValidacionIncidente validacion = new ValidacionIncidente();
        validacion.setUsuario(usuario);
        validacion.setIncidente(incidente);
        validacion.setTipo_voto(voto);
        validacion.setFecha(LocalDateTime.now());
        ValidacionIncidente guardada = validacionRepository.save(validacion);

        // HU-40: una denuncia de falsa alarma se notifica al autor del reporte y a los administradores
        if (voto.equals("FALSA_ALARMA")) {
            notificarDenuncia(incidente);
        }
        return aDTO(guardada);
    }

    private void notificarDenuncia(Incidente incidente) {
        String codigo = "#REP-" + incidente.getId_incidente();
        String tipo = incidente.getTipo_incidente() != null ? incidente.getTipo_incidente() : "reporte";
        if (incidente.getUsuario() != null) {
            notificacionService.crearParaUsuario(incidente.getUsuario(),
                    "Un vecino marcó tu reporte " + codigo + " (" + tipo + ") como inconsistente. Un administrador lo revisará.",
                    "FALSA_ALARMA", "MEDIA");
        }
        long denuncias = validacionRepository.contarVotos(incidente, "FALSA_ALARMA");
        for (Usuario admin : usuarioRepository.findByRol("ADMIN")) {
            notificacionService.crearParaUsuario(admin,
                    "El reporte " + codigo + " (" + tipo + ") fue denunciado como falsa alarma. Denuncias acumuladas: " + denuncias + ".",
                    "FALSA_ALARMA", "MEDIA");
        }
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
