package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;

public interface AlertaService {

    List<AlertaDTO> listar();

    AlertaDTO obtenerPorId(Long id);

    // HU-08 / HU-09: alerta SOS o silenciosa (tipo_alerta: SOS o SILENCIOSA)
    AlertaDTO registrar(Long id_usuario, AlertaDTO alertaDTO);

    // HU-15: alerta automática generada por un dispositivo IoT
    AlertaDTO registrarConDispositivo(Long id_usuario, Long id_dispositivo, AlertaDTO alertaDTO);

    // HU-12: historial de alertas
    List<AlertaDTO> listarPorUsuario(Long id_usuario);

    List<AlertaDTO> listarPorUsuarioYEstado(Long id_usuario, String estado);

    List<AlertaDTO> listarPorEstado(String estado);

    // HU-11: cancelar alerta
    AlertaDTO cancelar(Long id);

    AlertaDTO despachar(Long id, String codigo_despacho);

    AlertaDTO cambiarEstado(Long id, String estado);

    void eliminar(Long id);
}
