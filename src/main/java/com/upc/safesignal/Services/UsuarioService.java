package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;
import java.util.Map;

public interface UsuarioService {

    List<UsuarioDTO> listar();

    UsuarioDTO obtenerPorId(Long id);

    boolean existeCorreo(String correo);

    boolean existeDni(String dni);

    // HU-01: crear cuenta
    UsuarioDTO registrar(UsuarioDTO usuarioDTO);

    // HU-01: un administrador crea otra cuenta de administrador
    UsuarioDTO registrarAdmin(UsuarioDTO usuarioDTO);

    // HU-02: iniciar sesión
    UsuarioDTO iniciarSesion(String correo, String contrasena);

    // dni y rol son opcionales
    UsuarioDTO iniciarSesion(String correo, String dni, String contrasena, String rol);

    // HU-03 paso 1: verificar que la cuenta existe
    void verificarCuenta(String correo, String dni);

    // HU-03 paso 2: restablecer contraseña
    void restablecerContrasena(String correo, String dni, String nuevaContrasena);

    // HU-04: editar perfil
    UsuarioDTO actualizar(Long id, UsuarioDTO datos);

    // HU-49: personalizar el mensaje predeterminado de la alerta SOS
    UsuarioDTO actualizarMensajeSos(Long id, String mensaje);

    // HU-47: datos para los accesos rápidos de la pantalla principal
    Map<String, Object> accesosRapidos(Long id);

    // HU-48: suspender cuenta
    UsuarioDTO suspender(Long id, String motivo);

    // HU-48: reactivar cuenta
    UsuarioDTO reactivar(Long id);

    UsuarioDTO cambiarEstado(Long id, String estado, String motivo);

    List<UsuarioDTO> listarPorEstado(String estado);

    // HU-07: eliminar cuenta
    void eliminar(Long id);
}
