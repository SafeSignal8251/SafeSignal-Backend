package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispositivos-iot")
public class DispositivoIoTController {

    @Autowired
    private DispositivoIoTService dispositivoService;

    @GetMapping
    public ResponseEntity<List<DispositivoIoTDTO>> listar() {
        return ResponseEntity.ok(dispositivoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DispositivoIoTDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(dispositivoService.obtenerPorId(id));
    }

    // HU-31: vincular dispositivo
    @PostMapping
    public ResponseEntity<DispositivoIoTDTO> registrar(
            @RequestParam Long id_usuario,
            @RequestBody DispositivoIoTDTO dispositivoDTO) {
        return ResponseEntity.ok(dispositivoService.registrar(id_usuario, dispositivoDTO));
    }

    // HU-30: configurar nombre y estado (HABILITADO / DESHABILITADO)
    @PutMapping("/{id}")
    public ResponseEntity<DispositivoIoTDTO> actualizar(
            @PathVariable Long id,
            @RequestBody DispositivoIoTDTO dispositivoDTO) {
        return ResponseEntity.ok(dispositivoService.actualizar(id, dispositivoDTO));
    }

    // HU-34: tipos de anomalía a detectar (texto separado por comas)
    @PutMapping("/{id}/anomalias")
    public ResponseEntity<DispositivoIoTDTO> configurarAnomalias(
            @PathVariable Long id,
            @RequestParam String tipos_anomalia) {
        return ResponseEntity.ok(dispositivoService.configurarAnomalias(id, tipos_anomalia));
    }

    // HU-32: sincronización
    @PutMapping("/{id}/sincronizar")
    public ResponseEntity<DispositivoIoTDTO> sincronizar(@PathVariable Long id) {
        return ResponseEntity.ok(dispositivoService.sincronizar(id));
    }

    // HU-32: sincronización rápida de todos los dispositivos del usuario
    @PutMapping("/usuario/{id_usuario}/sincronizar")
    public ResponseEntity<List<DispositivoIoTDTO>> sincronizarTodos(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(dispositivoService.sincronizarTodos(id_usuario));
    }

    // HU-28: desvincular dispositivo
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        dispositivoService.eliminar(id);
    }

    // HU-33: dispositivos del usuario
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<DispositivoIoTDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(dispositivoService.listarPorUsuario(id_usuario));
    }
}
