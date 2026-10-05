package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    @Autowired
    private RutaService rutaService;

    @GetMapping
    public ResponseEntity<List<RutaDTO>> listar() {
        return ResponseEntity.ok(rutaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.obtenerPorId(id));
    }

    // HU-21: guardar ruta frecuente
    @PostMapping
    public ResponseEntity<RutaDTO> registrar(@RequestParam Long id_usuario, @RequestBody RutaDTO rutaDTO) {
        return ResponseEntity.ok(rutaService.registrar(id_usuario, rutaDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutaDTO> actualizar(@PathVariable Long id, @RequestBody RutaDTO rutaDTO) {
        return ResponseEntity.ok(rutaService.actualizar(id, rutaDTO));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        rutaService.eliminar(id);
    }

    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<RutaDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(rutaService.listarPorUsuario(id_usuario));
    }

    // HU-17: riesgo de una ruta
    @GetMapping("/{id}/riesgo")
    public ResponseEntity<Map<String, Object>> evaluarRiesgo(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.evaluarRiesgo(id));
    }

    // HU-17: rutas seguras del usuario
    @GetMapping("/usuario/{id_usuario}/seguras")
    public ResponseEntity<List<RutaDTO>> listarSeguras(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(rutaService.listarSeguras(id_usuario));
    }

    // HU-19: ruta alternativa automática
    @GetMapping("/{id}/alternativa")
    public ResponseEntity<RutaDTO> sugerirAlternativa(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.sugerirAlternativa(id));
    }
}
