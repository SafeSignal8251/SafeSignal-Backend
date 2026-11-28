package com.upc.safesignal.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Seguimientos", description = "Monitoreo y ubicación compartida en tiempo real del usuario.")
@RestController
@RequestMapping("/api/seguimientos")
public class SeguimientoController {

    @Autowired
    private SeguimientoService seguimientoService;

    @Operation(summary = "Listar seguimientos", description = "Devuelve todos los registros de seguimientos.")
    @GetMapping
    public ResponseEntity<List<SeguimientoDTO>> listar() {
        return ResponseEntity.ok(seguimientoService.listar());
    }

    @Operation(summary = "Obtener seguimiento por id", description = "Devuelve el registro de seguimiento con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<SeguimientoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(seguimientoService.obtenerPorId(id));
    }

    // HU-10 / HU-27: iniciar seguimiento (body: destino y tiempo_monitoreo)
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Iniciar un seguimiento", description = "Inicia el seguimiento con estado ACTIVO y el tiempo de monitoreo en minutos. Solo se permite un seguimiento activo por usuario.")
    @PostMapping
    public ResponseEntity<SeguimientoDTO> iniciar(
            @RequestParam Long id_usuario,
            @RequestBody SeguimientoDTO seguimientoDTO) {
        return ResponseEntity.ok(seguimientoService.iniciar(id_usuario, seguimientoDTO));
    }

    // HU-23: seguimiento automático al salir de una zona segura
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Iniciar el seguimiento automático al salir de una zona segura", description = "Inicia un seguimiento ACTIVO con la ubicación de salida de la zona segura.")
    @PostMapping("/automatico")
    public ResponseEntity<SeguimientoDTO> iniciarAutomatico(
            @RequestParam Long id_usuario,
            @RequestParam Double latitud,
            @RequestParam Double longitud) {
        return ResponseEntity.ok(seguimientoService.iniciarAutomatico(id_usuario, latitud, longitud));
    }

    @Operation(summary = "Finalizar un seguimiento", description = "Cambia el estado a FINALIZADO y guarda la fecha de fin.")
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<SeguimientoDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(seguimientoService.finalizar(id));
    }

    @Operation(summary = "Eliminar seguimiento", description = "Elimina el registro de seguimiento con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        seguimientoService.eliminar(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Listar seguimientos de un usuario", description = "Devuelve los registros de seguimientos del usuario indicado.")
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<SeguimientoDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(seguimientoService.listarPorUsuario(id_usuario));
    }

    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Listar los seguimientos activos de un usuario", description = "Devuelve los seguimientos con estado ACTIVO del usuario.")
    @GetMapping("/usuario/{id_usuario}/activos")
    public ResponseEntity<List<SeguimientoDTO>> listarActivosPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(seguimientoService.listarActivosPorUsuario(id_usuario));
    }

    @Operation(summary = "Listar seguimientos por estado", description = "Devuelve los seguimientos ACTIVO o FINALIZADO.")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SeguimientoDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(seguimientoService.listarPorEstado(estado));
    }
}
