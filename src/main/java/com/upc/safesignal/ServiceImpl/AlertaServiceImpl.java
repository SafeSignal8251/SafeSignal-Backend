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
public class AlertaServiceImpl implements AlertaService {

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DispositivoIoTRepository dispositivoRepository;

    @Autowired
    private ContactoConfianzaRepository contactoRepository;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<AlertaDTO> listar() {
        return aDTOs(alertaRepository.findAll());
    }

    @Override
    public AlertaDTO obtenerPorId(Long id) {
        return aDTO(buscarAlerta(id));
    }

    // HU-08 / HU-09: alerta SOS o silenciosa (tipo_alerta: SOS o SILENCIOSA)
    @Override
    public AlertaDTO registrar(Long id_usuario, AlertaDTO alertaDTO) {
        Usuario usuario = buscarUsuario(id_usuario);
        if (alertaDTO.getLatitud() == null || alertaDTO.getLongitud() == null) {
            throw new RuntimeException("La ubicación es obligatoria para activar una alerta");
        }
        Alerta alerta = modelMapper.map(alertaDTO, Alerta.class);
        if (vacio(alerta.getTipo_alerta())) {
            alerta.setTipo_alerta("SOS");
        } else {
            alerta.setTipo_alerta(alerta.getTipo_alerta().toUpperCase());
        }
        if (vacio(alerta.getOrigen())) {
            alerta.setOrigen("APP");
        }
        return aDTO(crear(usuario, null, alerta));
    }

    // HU-15: alerta automática generada por un dispositivo IoT
    @Override
    public AlertaDTO registrarConDispositivo(Long id_usuario, Long id_dispositivo, AlertaDTO alertaDTO) {
        Usuario usuario = buscarUsuario(id_usuario);
        DispositivoIoT dispositivo = dispositivoRepository.findById(id_dispositivo)
                .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));

        Alerta alerta = modelMapper.map(alertaDTO, Alerta.class);
        if (vacio(alerta.getTipo_alerta())) {
            alerta.setTipo_alerta("ANOMALIA");
        } else {
            alerta.setTipo_alerta(alerta.getTipo_alerta().toUpperCase());
        }
        alerta.setOrigen("DISPOSITIVO");
        return aDTO(crear(usuario, dispositivo, alerta));
    }

    // HU-12: historial de alertas
    @Override
    public List<AlertaDTO> listarPorUsuario(Long id_usuario) {
        return aDTOs(alertaRepository.historialPorUsuario(buscarUsuario(id_usuario)));
    }

    @Override
    public List<AlertaDTO> listarPorUsuarioYEstado(Long id_usuario, String estado) {
        return aDTOs(alertaRepository.findByUsuarioAndEstado(buscarUsuario(id_usuario), estado.toUpperCase()));
    }

    @Override
    public List<AlertaDTO> listarPorEstado(String estado) {
        return aDTOs(alertaRepository.findByEstado(estado.toUpperCase()));
    }

    // HU-11: cancelar alerta
    @Override
    public AlertaDTO cancelar(Long id) {
        Alerta alerta = buscarAlerta(id);
        if (!"ACTIVA".equals(alerta.getEstado())) {
            throw new RuntimeException("La alerta ya no está activa");
        }
        alerta.setEstado("CANCELADA");
        alerta.setFecha_cancelacion(LocalDateTime.now());
        log.info("Alerta cancelada, id: {}", id);
        return aDTO(alertaRepository.save(alerta));
    }

    @Override
    public AlertaDTO despachar(Long id, String codigo_despacho) {
        if (vacio(codigo_despacho)) {
            throw new RuntimeException("El código de despacho es obligatorio");
        }
        Alerta alerta = buscarAlerta(id);
        alerta.setEstado("DESPACHADA");
        alerta.setCodigo_despacho(codigo_despacho);
        log.info("Alerta despachada, id: {}", id);
        return aDTO(alertaRepository.save(alerta));
    }

    @Override
    public AlertaDTO cambiarEstado(Long id, String estado) {
        Alerta alerta = buscarAlerta(id);
        alerta.setEstado(estado.toUpperCase());
        return aDTO(alertaRepository.save(alerta));
    }

    @Override
    public void eliminar(Long id) {
        log.warn("Eliminando alerta con id: {}", id);
        alertaRepository.delete(buscarAlerta(id));
    }

    // ---------- métodos privados ----------

    // Guarda la alerta y notifica a los contactos de confianza
    private Alerta crear(Usuario usuario, DispositivoIoT dispositivo, Alerta alerta) {
        alerta.setId_alerta(null);
        alerta.setUsuario(usuario);
        alerta.setDispositivo(dispositivo);
        alerta.setFecha_hora(LocalDateTime.now());
        alerta.setEstado("ACTIVA");
        alerta.setFecha_cancelacion(null);
        Alerta guardada = alertaRepository.save(alerta);
        log.info("Alerta {} activada por el usuario id: {}", guardada.getTipo_alerta(), usuario.getId_usuario());

        String mensaje = "ALERTA " + guardada.getTipo_alerta() + " de "
                + usuario.getNombres() + " " + usuario.getApellidos();
        List<ContactoConfianza> contactos = contactoRepository.findByUsuarioOrderByPrioridadAsc(usuario);
        for (ContactoConfianza contacto : contactos) {
            notificacionService.crearParaContacto(guardada, contacto, mensaje);
        }
        notificacionService.crearParaUsuario(usuario,
                "Tu alerta fue enviada a " + contactos.size() + " contacto(s) de confianza",
                "ALERTA", "ALTA");
        return guardada;
    }

    private Alerta buscarAlerta(Long id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada con id: " + id));
    }

    private Usuario buscarUsuario(Long id_usuario) {
        return usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private AlertaDTO aDTO(Alerta alerta) {
        AlertaDTO dto = modelMapper.map(alerta, AlertaDTO.class);
        if (alerta.getUsuario() != null) {
            dto.setId_usuario(alerta.getUsuario().getId_usuario());
        }
        if (alerta.getDispositivo() != null) {
            dto.setId_dispositivo(alerta.getDispositivo().getId_dispositivo());
        }
        return dto;
    }

    private List<AlertaDTO> aDTOs(List<Alerta> alertas) {
        List<AlertaDTO> resultado = new ArrayList<>();
        for (Alerta alerta : alertas) {
            resultado.add(aDTO(alerta));
        }
        return resultado;
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
