package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seguimientos")
public class SeguimientoController {

    @Autowired
    private SeguimientoService seguimientoService;

    @GetMapping
    public ResponseEntity<List<SeguimientoDTO>> listar() {
        return ResponseEntity.ok(seguimientoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeguimientoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(seguimientoService.obtenerPorId(id));
    }

    // HU-10 / HU-27: iniciar seguimiento (body: destino y tiempo_monitoreo)
    @PostMapping
    public ResponseEntity<SeguimientoDTO> iniciar(
            @RequestParam Long id_usuario,
            @RequestBody SeguimientoDTO seguimientoDTO) {
        return ResponseEntity.ok(seguimientoService.iniciar(id_usuario, seguimientoDTO));
    }

    // HU-23: seguimiento automático al salir de una zona segura
    @PostMapping("/automatico")
    public ResponseEntity<SeguimientoDTO> iniciarAutomatico(
            @RequestParam Long id_usuario,
            @RequestParam Double latitud,
            @RequestParam Double longitud) {
        return ResponseEntity.ok(seguimientoService.iniciarAutomatico(id_usuario, latitud, longitud));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<SeguimientoDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(seguimientoService.finalizar(id));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        seguimientoService.eliminar(id);
    }

    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<SeguimientoDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(seguimientoService.listarPorUsuario(id_usuario));
    }

    @GetMapping("/usuario/{id_usuario}/activos")
    public ResponseEntity<List<SeguimientoDTO>> listarActivosPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(seguimientoService.listarActivosPorUsuario(id_usuario));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SeguimientoDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(seguimientoService.listarPorEstado(estado));
    }
}
