package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;

import java.util.List;

public interface NotificacionService {

    List<NotificacionDTO> listar();

    NotificacionDTO obtenerPorId(Long id);

    NotificacionDTO registrar(Long id_alerta, Long id_contacto, NotificacionDTO notificacionDTO);

    // Los 4 métodos siguientes los usan otros services (Alerta e Incidente), por eso reciben entidades
    void crearParaContacto(Alerta alerta, ContactoConfianza contacto, String mensaje);

    void crearParaUsuario(Usuario usuario, String mensaje, String tipo, String severidad);

    void crearParaUsuario(Usuario usuario, String mensaje, String tipo, String severidad, ZonaRiesgo zona);

    // HU-36: avisar a los suscriptores de una zona (respetando su horario)
    void notificarSuscriptores(ZonaRiesgo zona, String mensaje);

    // HU-26: aviso del administrador (id_zona null = todos los clientes). Devuelve cuántos lo recibieron
    int enviarAviso(Long id_admin, Long id_zona, String severidad, String mensaje);

    // HU-13: notificaciones de un usuario, las más recientes primero
    List<NotificacionDTO> listarPorUsuario(Long id_usuario);

    List<NotificacionDTO> listarPorAlerta(Long id_alerta);

    List<NotificacionDTO> listarPorContacto(Long id_contacto);

    NotificacionDTO cambiarEstado(Long id, String estado);

    void eliminar(Long id);
}
