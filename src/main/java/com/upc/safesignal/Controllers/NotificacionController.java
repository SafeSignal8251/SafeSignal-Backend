package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> listar() {
        return ResponseEntity.ok(notificacionService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<NotificacionDTO> registrar(
            @RequestParam Long id_alerta,
            @RequestParam Long id_contacto,
            @RequestBody NotificacionDTO notificacionDTO) {
        return ResponseEntity.ok(notificacionService.registrar(id_alerta, id_contacto, notificacionDTO));
    }

    // HU-26: aviso del administrador (sin id_zona = a todos los clientes). body: mensaje y severidad
    @PreAuthorize("hasRole('ADMIN')")
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
    @PutMapping("/{id}/estado")
    public ResponseEntity<NotificacionDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(notificacionService.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
    }

    // HU-13: bandeja de notificaciones del usuario
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<NotificacionDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(notificacionService.listarPorUsuario(id_usuario));
    }

    @GetMapping("/alerta/{id_alerta}")
    public ResponseEntity<List<NotificacionDTO>> listarPorAlerta(@PathVariable Long id_alerta) {
        return ResponseEntity.ok(notificacionService.listarPorAlerta(id_alerta));
    }

    @GetMapping("/contacto/{id_contacto}")
    public ResponseEntity<List<NotificacionDTO>> listarPorContacto(@PathVariable Long id_contacto) {
        return ResponseEntity.ok(notificacionService.listarPorContacto(id_contacto));
    }
}
