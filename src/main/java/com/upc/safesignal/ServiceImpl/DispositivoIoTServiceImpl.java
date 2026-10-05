package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.DispositivoIoTService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DispositivoIoTServiceImpl implements DispositivoIoTService {

    @Autowired
    private DispositivoIoTRepository dispositivoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<DispositivoIoTDTO> listar() {
        return aDTOs(dispositivoRepository.findAll());
    }

    @Override
    public DispositivoIoTDTO obtenerPorId(Long id) {
        return aDTO(buscarDispositivo(id));
    }

    // HU-31: vincular dispositivo
    @Override
    public DispositivoIoTDTO registrar(Long id_usuario, DispositivoIoTDTO dispositivoDTO) {
        Usuario usuario = buscarUsuario(id_usuario);

        if (dispositivoDTO.getCodigo_dispositivo() == null || dispositivoDTO.getCodigo_dispositivo().isBlank()) {
            throw new RuntimeException("El código del dispositivo es obligatorio");
        }
        if (!dispositivoRepository.findByCodigo(dispositivoDTO.getCodigo_dispositivo()).isEmpty()) {
            throw new RuntimeException("Ese dispositivo ya está vinculado");
        }

        DispositivoIoT dispositivo = modelMapper.map(dispositivoDTO, DispositivoIoT.class);
        dispositivo.setId_dispositivo(null);
        dispositivo.setUsuario(usuario);
        dispositivo.setFecha_vinculacion(LocalDateTime.now());
        dispositivo.setFecha_ultima(LocalDateTime.now());
        dispositivo.setEstado("HABILITADO");
        if (dispositivo.getNivel_bateria() == null) {
            dispositivo.setNivel_bateria(100);
        }
        return aDTO(dispositivoRepository.save(dispositivo));
    }

    // HU-33: dispositivos del usuario (estado y batería)
    @Override
    public List<DispositivoIoTDTO> listarPorUsuario(Long id_usuario) {
        return aDTOs(dispositivoRepository.findByUsuario(buscarUsuario(id_usuario)));
    }

    // HU-30: configurar nombre y estado (HABILITADO / DESHABILITADO)
    @Override
    public DispositivoIoTDTO actualizar(Long id, DispositivoIoTDTO datos) {
        DispositivoIoT dispositivo = buscarDispositivo(id);
        if (datos.getNombre() != null) dispositivo.setNombre(datos.getNombre());
        if (datos.getEstado() != null) dispositivo.setEstado(datos.getEstado().toUpperCase());
        if (datos.getNivel_bateria() != null) dispositivo.setNivel_bateria(datos.getNivel_bateria());
        dispositivo.setFecha_ultima(LocalDateTime.now());
        return aDTO(dispositivoRepository.save(dispositivo));
    }

    // HU-34: tipos de anomalía a detectar (texto separado por comas)
    @Override
    public DispositivoIoTDTO configurarAnomalias(Long id, String tipos_anomalia) {
        DispositivoIoT dispositivo = buscarDispositivo(id);
        dispositivo.setTipos_anomalia(tipos_anomalia);
        return aDTO(dispositivoRepository.save(dispositivo));
    }

    // HU-32: sincronización de un dispositivo
    @Override
    public DispositivoIoTDTO sincronizar(Long id) {
        DispositivoIoT dispositivo = buscarDispositivo(id);
        dispositivo.setFecha_ultima(LocalDateTime.now());
        return aDTO(dispositivoRepository.save(dispositivo));
    }

    // HU-32: sincronización rápida de todos los dispositivos habilitados del usuario
    @Override
    public List<DispositivoIoTDTO> sincronizarTodos(Long id_usuario) {
        Usuario usuario = buscarUsuario(id_usuario);
        List<DispositivoIoT> dispositivos = dispositivoRepository.findByUsuarioAndEstado(usuario, "HABILITADO");
        if (dispositivos.isEmpty()) {
            throw new RuntimeException("No tienes dispositivos habilitados para sincronizar");
        }
        for (DispositivoIoT d : dispositivos) {
            d.setFecha_ultima(LocalDateTime.now());
            dispositivoRepository.save(d);
        }
        return aDTOs(dispositivos);
    }

    @Override
    public DispositivoIoTDTO cambiarEstado(Long id, String estado) {
        DispositivoIoT dispositivo = buscarDispositivo(id);
        dispositivo.setEstado(estado.toUpperCase());
        return aDTO(dispositivoRepository.save(dispositivo));
    }

    // HU-28: desvincular
    @Override
    public void eliminar(Long id) {
        dispositivoRepository.delete(buscarDispositivo(id));
    }

    // ---------- métodos privados ----------

    private DispositivoIoT buscarDispositivo(Long id) {
        return dispositivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado con id: " + id));
    }

    private Usuario buscarUsuario(Long id_usuario) {
        return usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private DispositivoIoTDTO aDTO(DispositivoIoT dispositivo) {
        DispositivoIoTDTO dto = modelMapper.map(dispositivo, DispositivoIoTDTO.class);
        if (dispositivo.getUsuario() != null) {
            dto.setId_usuario(dispositivo.getUsuario().getId_usuario());
        }
        return dto;
    }

    private List<DispositivoIoTDTO> aDTOs(List<DispositivoIoT> dispositivos) {
        List<DispositivoIoTDTO> resultado = new ArrayList<>();
        for (DispositivoIoT dispositivo : dispositivos) {
            resultado.add(aDTO(dispositivo));
        }
        return resultado;
    }
}
