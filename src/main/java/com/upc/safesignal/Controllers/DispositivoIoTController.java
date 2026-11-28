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

@Tag(name = "Dispositivos IoT", description = "Dispositivos y sensores vinculados al usuario.")
@RestController
@RequestMapping("/api/dispositivos-iot")
public class DispositivoIoTController {

    @Autowired
    private DispositivoIoTService dispositivoService;

    @Operation(summary = "Listar dispositivos IoT", description = "Devuelve todos los registros de dispositivos IoT.")
    @GetMapping
    public ResponseEntity<List<DispositivoIoTDTO>> listar() {
        return ResponseEntity.ok(dispositivoService.listar());
    }

    @Operation(summary = "Obtener dispositivo IoT por id", description = "Devuelve el registro de dispositivo IoT con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<DispositivoIoTDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(dispositivoService.obtenerPorId(id));
    }

    // HU-31: vincular dispositivo
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Vincular un dispositivo IoT", description = "Vincula el dispositivo al usuario con estado inicial HABILITADO. Rechaza un código de dispositivo que ya esté vinculado.")
    @PostMapping
    public ResponseEntity<DispositivoIoTDTO> registrar(
            @RequestParam Long id_usuario,
            @RequestBody DispositivoIoTDTO dispositivoDTO) {
        return ResponseEntity.ok(dispositivoService.registrar(id_usuario, dispositivoDTO));
    }

    // HU-30: configurar nombre y estado (HABILITADO / DESHABILITADO)
    @Operation(summary = "Actualizar el nombre y el estado de un dispositivo", description = "Guarda el nombre y el estado (HABILITADO o DESHABILITADO) del dispositivo.")
    @PutMapping("/{id}")
    public ResponseEntity<DispositivoIoTDTO> actualizar(
            @PathVariable Long id,
            @RequestBody DispositivoIoTDTO dispositivoDTO) {
        return ResponseEntity.ok(dispositivoService.actualizar(id, dispositivoDTO));
    }

    // HU-34: tipos de anomalía a detectar (texto separado por comas)
    @Operation(summary = "Configurar los tipos de anomalía a detectar", description = "Guarda la lista de tipos de anomalía separados por comas (por ejemplo DESVIO_RUTA,VELOCIDAD_ALTA).")
    @PutMapping("/{id}/anomalias")
    public ResponseEntity<DispositivoIoTDTO> configurarAnomalias(
            @PathVariable Long id,
            @RequestParam String tipos_anomalia) {
        return ResponseEntity.ok(dispositivoService.configurarAnomalias(id, tipos_anomalia));
    }

    // HU-32: sincronización
    @Operation(summary = "Sincronizar un dispositivo", description = "Actualiza la fecha de última sincronización del dispositivo.")
    @PutMapping("/{id}/sincronizar")
    public ResponseEntity<DispositivoIoTDTO> sincronizar(@PathVariable Long id) {
        return ResponseEntity.ok(dispositivoService.sincronizar(id));
    }

    // HU-32: sincronización rápida de todos los dispositivos del usuario
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Sincronizar todos los dispositivos de un usuario", description = "Actualiza la fecha de última sincronización de todos los dispositivos del usuario.")
    @PutMapping("/usuario/{id_usuario}/sincronizar")
    public ResponseEntity<List<DispositivoIoTDTO>> sincronizarTodos(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(dispositivoService.sincronizarTodos(id_usuario));
    }

    // HU-28: desvincular dispositivo
    @Operation(summary = "Eliminar dispositivo IoT", description = "Elimina el registro de dispositivo IoT con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        dispositivoService.eliminar(id);
    }

    // HU-33: dispositivos del usuario
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Listar dispositivos IoT de un usuario", description = "Devuelve los registros de dispositivos IoT del usuario indicado.")
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<DispositivoIoTDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(dispositivoService.listarPorUsuario(id_usuario));
    }
}
