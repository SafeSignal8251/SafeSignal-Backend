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
import java.util.Map;

@Tag(name = "Rutas", description = "Rutas frecuentes del usuario, evaluación de su riesgo y ruta alternativa más segura.")
@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    @Autowired
    private RutaService rutaService;

    @Operation(summary = "Listar rutas", description = "Devuelve todos los registros de rutas.")
    @GetMapping
    public ResponseEntity<List<RutaDTO>> listar() {
        return ResponseEntity.ok(rutaService.listar());
    }

    @Operation(summary = "Obtener ruta por id", description = "Devuelve el registro de ruta con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<RutaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.obtenerPorId(id));
    }

    // HU-21: guardar ruta frecuente
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Guardar una ruta", description = "Guarda la ruta (nombre, origen, destino, coordenadas, distancia y tiempo estimado). El origen y el destino son obligatorios.")
    @PostMapping
    public ResponseEntity<RutaDTO> registrar(@RequestParam Long id_usuario, @RequestBody RutaDTO rutaDTO) {
        return ResponseEntity.ok(rutaService.registrar(id_usuario, rutaDTO));
    }

    @Operation(summary = "Actualizar una ruta", description = "Actualiza los datos de la ruta, por ejemplo la distancia y el tiempo reales calculados por calles.")
    @PutMapping("/{id}")
    public ResponseEntity<RutaDTO> actualizar(@PathVariable Long id, @RequestBody RutaDTO rutaDTO) {
        return ResponseEntity.ok(rutaService.actualizar(id, rutaDTO));
    }

    @Operation(summary = "Eliminar ruta", description = "Elimina el registro de ruta con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        rutaService.eliminar(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Listar rutas de un usuario", description = "Devuelve los registros de rutas del usuario indicado.")
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<RutaDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(rutaService.listarPorUsuario(id_usuario));
    }

    // HU-17: riesgo de una ruta
    @Operation(summary = "Evaluar el riesgo de una ruta", description = "Cuenta los incidentes dentro del rectángulo origen-destino (con un margen de unos 300 m). Devuelve incidentes_cercanos y nivel_riesgo: BAJO con menos de 6 incidentes, MEDIO de 6 a 14 y ALTO desde 15.")
    @GetMapping("/{id}/riesgo")
    public ResponseEntity<Map<String, Object>> evaluarRiesgo(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.evaluarRiesgo(id));
    }

    // HU-17: rutas seguras del usuario
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Listar las rutas seguras de un usuario", description = "Devuelve las rutas guardadas del usuario cuyo nivel de riesgo es BAJO.")
    @GetMapping("/usuario/{id_usuario}/seguras")
    public ResponseEntity<List<RutaDTO>> listarSeguras(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(rutaService.listarSeguras(id_usuario));
    }

    // HU-19: ruta alternativa automática
    @Operation(summary = "Sugerir una ruta alternativa más segura", description = "Si la ruta tiene riesgo MEDIO o ALTO, busca entre las rutas del usuario con el mismo destino la de menos incidentes. Responde con error si la ruta ya es segura o no existe una mejor.")
    @GetMapping("/{id}/alternativa")
    public ResponseEntity<RutaDTO> sugerirAlternativa(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.sugerirAlternativa(id));
    }
}
