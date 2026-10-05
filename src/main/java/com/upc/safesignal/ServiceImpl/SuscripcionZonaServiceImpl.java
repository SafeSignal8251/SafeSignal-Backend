package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.SuscripcionZonaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SuscripcionZonaServiceImpl implements SuscripcionZonaService {

    @Autowired
    private SuscripcionZonaRepository suscripcionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ZonaRiesgoRepository zonaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<SuscripcionZonaDTO> listar() {
        return aDTOs(suscripcionRepository.findAll());
    }

    @Override
    public SuscripcionZonaDTO obtenerPorId(Long id) {
        return aDTO(buscarSuscripcion(id));
    }

    // HU-36: datos trae etiqueta, punto, radio, categorías, canales y horario (todo opcional)
    @Override
    public SuscripcionZonaDTO registrar(Long id_usuario, Long id_zona, SuscripcionZonaDTO datos) {
        Usuario usuario = buscarUsuario(id_usuario);
        ZonaRiesgo zona = buscarZona(id_zona);

        if (!suscripcionRepository.findByUsuarioAndZona(usuario, zona).isEmpty()) {
            throw new RuntimeException("Ya estás suscrito a esta zona");
        }

        SuscripcionZona s = new SuscripcionZona();
        s.setUsuario(usuario);
        s.setZona(zona);
        s.setFecha_suscripcion(LocalDateTime.now());

        if (datos.getEtiqueta() != null) {
            s.setEtiqueta(datos.getEtiqueta());
        } else {
            s.setEtiqueta(zona.getNombre());
        }
        if (datos.getLatitud() != null) {
            s.setLatitud(datos.getLatitud());
        } else {
            s.setLatitud(zona.getLatitud());
        }
        if (datos.getLongitud() != null) {
            s.setLongitud(datos.getLongitud());
        } else {
            s.setLongitud(zona.getLongitud());
        }
        if (datos.getRadio_metros() != null) {
            s.setRadio_metros(datos.getRadio_metros());
        } else {
            s.setRadio_metros(zona.getRadio_metros());
        }
        s.setCategorias(datos.getCategorias());
        s.setHora_desde(datos.getHora_desde());
        s.setHora_hasta(datos.getHora_hasta());

        // por defecto: push activado; whatsapp/sms y correo desactivados
        if (datos.getNotificar_push() == null) {
            s.setNotificar_push(true);
        } else {
            s.setNotificar_push(datos.getNotificar_push());
        }
        if (datos.getNotificar_whatsapp_sms() == null) {
            s.setNotificar_whatsapp_sms(false);
        } else {
            s.setNotificar_whatsapp_sms(datos.getNotificar_whatsapp_sms());
        }
        if (datos.getNotificar_correo() == null) {
            s.setNotificar_correo(false);
        } else {
            s.setNotificar_correo(datos.getNotificar_correo());
        }
        return aDTO(suscripcionRepository.save(s));
    }

    @Override
    public SuscripcionZonaDTO actualizar(Long id, SuscripcionZonaDTO datos) {
        SuscripcionZona s = buscarSuscripcion(id);
        if (datos.getEtiqueta() != null) s.setEtiqueta(datos.getEtiqueta());
        if (datos.getRadio_metros() != null) s.setRadio_metros(datos.getRadio_metros());
        if (datos.getCategorias() != null) s.setCategorias(datos.getCategorias());
        if (datos.getNotificar_push() != null) s.setNotificar_push(datos.getNotificar_push());
        if (datos.getNotificar_whatsapp_sms() != null) s.setNotificar_whatsapp_sms(datos.getNotificar_whatsapp_sms());
        if (datos.getNotificar_correo() != null) s.setNotificar_correo(datos.getNotificar_correo());
        if (datos.getHora_desde() != null) s.setHora_desde(datos.getHora_desde());
        if (datos.getHora_hasta() != null) s.setHora_hasta(datos.getHora_hasta());
        return aDTO(suscripcionRepository.save(s));
    }

    @Override
    public List<SuscripcionZonaDTO> listarPorUsuario(Long id_usuario) {
        return aDTOs(suscripcionRepository.findByUsuario(buscarUsuario(id_usuario)));
    }

    @Override
    public List<SuscripcionZonaDTO> listarPorZona(Long id_zona) {
        return aDTOs(suscripcionRepository.findByZona(buscarZona(id_zona)));
    }

    @Override
    public List<SuscripcionZonaDTO> listarPorUsuarioYZona(Long id_usuario, Long id_zona) {
        return aDTOs(suscripcionRepository.findByUsuarioAndZona(buscarUsuario(id_usuario), buscarZona(id_zona)));
    }

    @Override
    public void eliminar(Long id) {
        suscripcionRepository.delete(buscarSuscripcion(id));
    }

    // ---------- métodos privados ----------

    private SuscripcionZona buscarSuscripcion(Long id) {
        return suscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada con id: " + id));
    }

    private Usuario buscarUsuario(Long id_usuario) {
        return usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private ZonaRiesgo buscarZona(Long id_zona) {
        return zonaRepository.findById(id_zona)
                .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada"));
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private SuscripcionZonaDTO aDTO(SuscripcionZona s) {
        SuscripcionZonaDTO dto = modelMapper.map(s, SuscripcionZonaDTO.class);
        if (s.getUsuario() != null) {
            dto.setId_usuario(s.getUsuario().getId_usuario());
        }
        if (s.getZona() != null) {
            dto.setId_zona(s.getZona().getId_zona());
        }
        return dto;
    }

    private List<SuscripcionZonaDTO> aDTOs(List<SuscripcionZona> suscripciones) {
        List<SuscripcionZonaDTO> resultado = new ArrayList<>();
        for (SuscripcionZona s : suscripciones) {
            resultado.add(aDTO(s));
        }
        return resultado;
    }
}
