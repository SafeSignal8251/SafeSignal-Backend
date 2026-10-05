package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.NotificacionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private ContactoConfianzaRepository contactoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ZonaRiesgoRepository zonaRepository;

    @Autowired
    private SuscripcionZonaRepository suscripcionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<NotificacionDTO> listar() {
        return aDTOs(notificacionRepository.findAll());
    }

    @Override
    public NotificacionDTO obtenerPorId(Long id) {
        return aDTO(buscarNotificacion(id));
    }

    @Override
    public NotificacionDTO registrar(Long id_alerta, Long id_contacto, NotificacionDTO notificacionDTO) {
        Alerta alerta = alertaRepository.findById(id_alerta)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        ContactoConfianza contacto = contactoRepository.findById(id_contacto)
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado"));

        if (notificacionDTO.getMensaje() == null || notificacionDTO.getMensaje().isBlank()) {
            throw new RuntimeException("El mensaje es obligatorio");
        }
        Notificacion notificacion = modelMapper.map(notificacionDTO, Notificacion.class);
        notificacion.setId_notificacion(null);
        notificacion.setAlerta(alerta);
        notificacion.setContacto(contacto);
        notificacion.setFecha_envio(LocalDateTime.now());
        notificacion.setEstado("ENVIADA");
        if (notificacion.getTipo() == null) {
            notificacion.setTipo("ALERTA");
        }
        return aDTO(notificacionRepository.save(notificacion));
    }

    // Notificación a un contacto de confianza por una alerta
    @Override
    public void crearParaContacto(Alerta alerta, ContactoConfianza contacto, String mensaje) {
        Notificacion n = new Notificacion();
        n.setMensaje(truncar(mensaje));
        n.setFecha_envio(LocalDateTime.now());
        n.setEstado("ENVIADA");
        n.setTipo("ALERTA");
        n.setSeveridad("ALTA");
        n.setAlerta(alerta);
        n.setContacto(contacto);
        notificacionRepository.save(n);
    }

    // Notificación dirigida a un usuario (aparece en su bandeja)
    @Override
    public void crearParaUsuario(Usuario usuario, String mensaje, String tipo, String severidad) {
        crearParaUsuario(usuario, mensaje, tipo, severidad, null);
    }

    @Override
    public void crearParaUsuario(Usuario usuario, String mensaje, String tipo,
                                 String severidad, ZonaRiesgo zona) {
        Notificacion n = new Notificacion();
        n.setMensaje(truncar(mensaje));
        n.setFecha_envio(LocalDateTime.now());
        n.setEstado("ENVIADA");
        n.setTipo(tipo);
        n.setSeveridad(severidad);
        n.setUsuario(usuario);
        n.setZona(zona);
        notificacionRepository.save(n);
    }

    // HU-36: avisar a los suscriptores de una zona (respetando su horario)
    @Override
    public void notificarSuscriptores(ZonaRiesgo zona, String mensaje) {
        LocalTime ahora = LocalTime.now();
        for (SuscripcionZona s : suscripcionRepository.findByZona(zona)) {
            if (dentroDeHorario(s.getHora_desde(), s.getHora_hasta(), ahora)) {
                crearParaUsuario(s.getUsuario(), mensaje, "REPORTE_ZONA", "MEDIA", zona);
            }
        }
    }

    // HU-26: aviso del administrador (id_zona null = todos los clientes). Devuelve cuántos lo recibieron
    @Override
    public int enviarAviso(Long id_admin, Long id_zona, String severidad, String mensaje) {
        Usuario admin = usuarioRepository.findById(id_admin)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!"ADMIN".equalsIgnoreCase(admin.getRol())) {
            throw new RuntimeException("Solo un administrador puede enviar avisos");
        }
        if (mensaje == null || mensaje.isBlank()) {
            throw new RuntimeException("El mensaje es obligatorio");
        }
        if (mensaje.length() > 200) {
            throw new RuntimeException("El mensaje no puede superar los 200 caracteres");
        }

        ZonaRiesgo zona = null;
        List<Usuario> destinatarios = new ArrayList<>();
        if (id_zona != null) {
            zona = zonaRepository.findById(id_zona)
                    .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada"));
            for (SuscripcionZona s : suscripcionRepository.findByZona(zona)) {
                destinatarios.add(s.getUsuario());
            }
        } else {
            destinatarios = usuarioRepository.findByRol("CLIENTE");
        }

        String nivel = "MEDIA";
        if (severidad != null && !severidad.isBlank()) {
            nivel = severidad.toUpperCase();
        }
        for (Usuario destinatario : destinatarios) {
            crearParaUsuario(destinatario, mensaje, "AVISO", nivel, zona);
        }
        return destinatarios.size();
    }

    // HU-13: notificaciones de un usuario, las más recientes primero
    @Override
    public List<NotificacionDTO> listarPorUsuario(Long id_usuario) {
        Usuario usuario = usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return aDTOs(notificacionRepository.findByUsuarioReciente(usuario));
    }

    @Override
    public List<NotificacionDTO> listarPorAlerta(Long id_alerta) {
        Alerta alerta = alertaRepository.findById(id_alerta)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        return aDTOs(notificacionRepository.findByAlerta(alerta));
    }

    @Override
    public List<NotificacionDTO> listarPorContacto(Long id_contacto) {
        ContactoConfianza contacto = contactoRepository.findById(id_contacto)
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado"));
        return aDTOs(notificacionRepository.findByContacto(contacto));
    }

    @Override
    public NotificacionDTO cambiarEstado(Long id, String estado) {
        Notificacion notificacion = buscarNotificacion(id);
        notificacion.setEstado(estado.toUpperCase());
        return aDTO(notificacionRepository.save(notificacion));
    }

    @Override
    public void eliminar(Long id) {
        notificacionRepository.delete(buscarNotificacion(id));
    }

    // ---------- métodos privados ----------

    private Notificacion buscarNotificacion(Long id) {
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con id: " + id));
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private NotificacionDTO aDTO(Notificacion n) {
        NotificacionDTO dto = modelMapper.map(n, NotificacionDTO.class);
        if (n.getUsuario() != null) {
            dto.setId_usuario(n.getUsuario().getId_usuario());
        }
        if (n.getZona() != null) {
            dto.setId_zona(n.getZona().getId_zona());
        }
        if (n.getAlerta() != null) {
            dto.setId_alerta(n.getAlerta().getId_alerta());
        }
        if (n.getContacto() != null) {
            dto.setId_contacto(n.getContacto().getId_contacto());
        }
        return dto;
    }

    private List<NotificacionDTO> aDTOs(List<Notificacion> notificaciones) {
        List<NotificacionDTO> resultado = new ArrayList<>();
        for (Notificacion n : notificaciones) {
            resultado.add(aDTO(n));
        }
        return resultado;
    }

    private boolean dentroDeHorario(LocalTime desde, LocalTime hasta, LocalTime ahora) {
        if (desde == null || hasta == null) {
            return true;
        }
        if (desde.isBefore(hasta)) {
            return !ahora.isBefore(desde) && ahora.isBefore(hasta);
        }
        // horario nocturno (por ejemplo 18:00 a 07:00)
        return !ahora.isBefore(desde) || ahora.isBefore(hasta);
    }

    private String truncar(String mensaje) {
        if (mensaje.length() > 255) {
            return mensaje.substring(0, 255);
        }
        return mensaje;
    }
}
