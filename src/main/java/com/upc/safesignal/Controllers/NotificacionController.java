package com.upc.safesignal.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notificaciones", description = "Notificaciones a los usuarios y a sus contactos de confianza, y avisos del administrador.")
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Operation(summary = "Listar notificaciones", description = "Devuelve todos los registros de notificaciones.")
    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> listar() {
        return ResponseEntity.ok(notificacionService.listar());
    }

    @Operation(summary = "Obtener notificación por id", description = "Devuelve el registro de notificación con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    @Operation(summary = "Registrar una notificación para un contacto", description = "Crea una notificación para un contacto de confianza asociada a una alerta.")
    @PostMapping
    public ResponseEntity<NotificacionDTO> registrar(
            @RequestParam Long id_alerta,
            @RequestParam Long id_contacto,
            @RequestBody NotificacionDTO notificacionDTO) {
        return ResponseEntity.ok(notificacionService.registrar(id_alerta, id_contacto, notificacionDTO));
    }

    // HU-26: aviso del administrador (sin id_zona = a todos los clientes). body: mensaje y severidad
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enviar un aviso a los vecinos (administrador)", description = "Envía un aviso (mensaje de hasta 200 caracteres y severidad). Si se indica id_zona lo reciben los suscriptores de esa zona; si no, todos los clientes. Solo un ADMIN puede hacerlo.")
    @PostMapping("/aviso")
    public ResponseEntity<String> enviarAviso(
            @RequestParam Long id_admin,
            @RequestParam(required = false) Long id_zona,
            @RequestBody NotificacionDTO avisoDTO) {
        int cantidad = notificacionService.enviarAviso(
                id_admin, id_zona, avisoDTO.getSeveridad(), avisoDTO.getMensaje());
        return ResponseEntity.ok("Aviso enviado a " + cantidad + " usuario(s)");
    }

    // por ejemplo estado = LEIDA
    @Operation(summary = "Cambiar el estado de una notificación", description = "Cambia el estado a ENVIADA o LEIDA (por ejemplo, para marcarla como leída).")
    @PutMapping("/{id}/estado")
    public ResponseEntity<NotificacionDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(notificacionService.cambiarEstado(id, estado));
    }

    @Operation(summary = "Eliminar notificación", description = "Elimina el registro de notificación con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
    }

    // HU-13: bandeja de notificaciones del usuario
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Bandeja de notificaciones de un usuario", description = "Devuelve las notificaciones del usuario, de la más reciente a la más antigua.")
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<NotificacionDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(notificacionService.listarPorUsuario(id_usuario));
    }

    @Operation(summary = "Listar las notificaciones de una alerta", description = "Devuelve las notificaciones generadas por la alerta indicada.")
    @GetMapping("/alerta/{id_alerta}")
    public ResponseEntity<List<NotificacionDTO>> listarPorAlerta(@PathVariable Long id_alerta) {
        return ResponseEntity.ok(notificacionService.listarPorAlerta(id_alerta));
    }

    @Operation(summary = "Listar las notificaciones de un contacto", description = "Devuelve las notificaciones enviadas al contacto de confianza indicado.")
    @GetMapping("/contacto/{id_contacto}")
    public ResponseEntity<List<NotificacionDTO>> listarPorContacto(@PathVariable Long id_contacto) {
        return ResponseEntity.ok(notificacionService.listarPorContacto(id_contacto));
    }
}
