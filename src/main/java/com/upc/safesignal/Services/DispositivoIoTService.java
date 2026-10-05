package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;

public interface DispositivoIoTService {

    List<DispositivoIoTDTO> listar();

    DispositivoIoTDTO obtenerPorId(Long id);

    // HU-31: vincular dispositivo
    DispositivoIoTDTO registrar(Long id_usuario, DispositivoIoTDTO dispositivoDTO);

    // HU-33: dispositivos del usuario (estado y batería)
    List<DispositivoIoTDTO> listarPorUsuario(Long id_usuario);

    // HU-30: configurar nombre y estado (HABILITADO / DESHABILITADO)
    DispositivoIoTDTO actualizar(Long id, DispositivoIoTDTO datos);

    // HU-34: tipos de anomalía a detectar (texto separado por comas)
    DispositivoIoTDTO configurarAnomalias(Long id, String tipos_anomalia);

    // HU-32: sincronización de un dispositivo
    DispositivoIoTDTO sincronizar(Long id);

    // HU-32: sincronización rápida de todos los dispositivos habilitados del usuario
    List<DispositivoIoTDTO> sincronizarTodos(Long id_usuario);

    DispositivoIoTDTO cambiarEstado(Long id, String estado);

    // HU-28: desvincular
    void eliminar(Long id);
}
