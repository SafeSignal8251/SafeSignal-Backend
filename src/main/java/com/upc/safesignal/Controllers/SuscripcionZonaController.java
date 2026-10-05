package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suscripciones-zona")
public class SuscripcionZonaController {

    @Autowired
    private SuscripcionZonaService suscripcionService;

    @GetMapping
    public ResponseEntity<List<SuscripcionZonaDTO>> listar() {
        return ResponseEntity.ok(suscripcionService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuscripcionZonaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(suscripcionService.obtenerPorId(id));
    }

    // HU-36: suscribirse a una zona
    // body (todo opcional): etiqueta, latitud, longitud, radio_metros, categorias,
    // notificar_push, notificar_whatsapp_sms, notificar_correo, hora_desde, hora_hasta
    @PostMapping
    public ResponseEntity<SuscripcionZonaDTO> registrar(
            @RequestParam Long id_usuario,
            @RequestParam Long id_zona,
            @RequestBody SuscripcionZonaDTO suscripcionDTO) {
        return ResponseEntity.ok(suscripcionService.registrar(id_usuario, id_zona, suscripcionDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuscripcionZonaDTO> actualizar(
            @PathVariable Long id,
            @RequestBody SuscripcionZonaDTO suscripcionDTO) {
        return ResponseEntity.ok(suscripcionService.actualizar(id, suscripcionDTO));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        suscripcionService.eliminar(id);
    }

    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<SuscripcionZonaDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(suscripcionService.listarPorUsuario(id_usuario));
    }

    @GetMapping("/zona/{id_zona}")
    public ResponseEntity<List<SuscripcionZonaDTO>> listarPorZona(@PathVariable Long id_zona) {
        return ResponseEntity.ok(suscripcionService.listarPorZona(id_zona));
    }

    @GetMapping("/usuario/{id_usuario}/zona/{id_zona}")
    public ResponseEntity<List<SuscripcionZonaDTO>> listarPorUsuarioYZona(
            @PathVariable Long id_usuario,
            @PathVariable Long id_zona) {
        return ResponseEntity.ok(suscripcionService.listarPorUsuarioYZona(id_usuario, id_zona));
    }
}
