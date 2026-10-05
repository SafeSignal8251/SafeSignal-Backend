package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    @Autowired
    private AlertaService alertaService;

    @GetMapping
    public ResponseEntity<List<AlertaDTO>> listar() {
        return ResponseEntity.ok(alertaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.obtenerPorId(id));
    }

    // HU-08 / HU-09: alerta SOS o silenciosa (body: latitud, longitud y tipo_alerta SOS o SILENCIOSA)
    @PostMapping
    public ResponseEntity<AlertaDTO> registrar(@RequestParam Long id_usuario, @RequestBody AlertaDTO alertaDTO) {
        return ResponseEntity.ok(alertaService.registrar(id_usuario, alertaDTO));
    }

    // HU-15: alerta automática de un dispositivo IoT
    @PostMapping("/dispositivo")
    public ResponseEntity<AlertaDTO> registrarConDispositivo(
            @RequestParam Long id_usuario,
            @RequestParam Long id_dispositivo,
            @RequestBody AlertaDTO alertaDTO) {
        return ResponseEntity.ok(alertaService.registrarConDispositivo(id_usuario, id_dispositivo, alertaDTO));
    }

    // HU-11: cancelar alerta
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<AlertaDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.cancelar(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/despachar")
    public ResponseEntity<AlertaDTO> despachar(@PathVariable Long id, @RequestParam String codigo_despacho) {
        return ResponseEntity.ok(alertaService.despachar(id, codigo_despacho));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/estado")
    public ResponseEntity<AlertaDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(alertaService.cambiarEstado(id, estado));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        alertaService.eliminar(id);
    }

    // HU-12: historial de alertas
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<AlertaDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(alertaService.listarPorUsuario(id_usuario));
    }

    @GetMapping("/usuario/{id_usuario}/estado/{estado}")
    public ResponseEntity<List<AlertaDTO>> listarPorUsuarioYEstado(
            @PathVariable Long id_usuario,
            @PathVariable String estado) {
        return ResponseEntity.ok(alertaService.listarPorUsuarioYEstado(id_usuario, estado));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<AlertaDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(alertaService.listarPorEstado(estado));
    }
}
