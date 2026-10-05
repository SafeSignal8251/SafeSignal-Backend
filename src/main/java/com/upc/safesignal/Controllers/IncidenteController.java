package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidentes")
public class IncidenteController {

    @Autowired
    private IncidenteService incidenteService;

    @GetMapping
    public ResponseEntity<List<IncidenteDTO>> listar() {
        return ResponseEntity.ok(incidenteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidenteDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(incidenteService.obtenerPorId(id));
    }

    // HU-16 / HU-22: registrar incidente
    @PostMapping
    public ResponseEntity<IncidenteDTO> registrar(
            @RequestParam Long id_zona,
            @RequestParam Long id_usuario,
            @RequestBody IncidenteDTO incidenteDTO) {
        return ResponseEntity.ok(incidenteService.registrar(id_usuario, id_zona, incidenteDTO));
    }

    // HU-16: reporte de incidente post-emergencia
    @PostMapping("/post-emergencia")
    public ResponseEntity<IncidenteDTO> registrarPostEmergencia(
            @RequestParam Long id_usuario,
            @RequestParam Long id_alerta,
            @RequestParam String descripcion) {
        return ResponseEntity.ok(incidenteService.registrarPostEmergencia(id_usuario, id_alerta, descripcion));
    }

    // HU-37: editar un reporte propio (body: descripcion)
    @PutMapping("/{id}")
    public ResponseEntity<IncidenteDTO> actualizar(
            @PathVariable Long id,
            @RequestParam Long id_usuario,
            @RequestBody IncidenteDTO incidenteDTO) {
        return ResponseEntity.ok(incidenteService.actualizarDescripcion(id, id_usuario, incidenteDTO.getDescripcion()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/estado")
    public ResponseEntity<IncidenteDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(incidenteService.cambiarEstado(id, estado));
    }

    // HU-39 / HU-46: descartar (descartar=true) o mantener (descartar=false)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/moderar")
    public ResponseEntity<IncidenteDTO> moderar(@PathVariable Long id, @RequestParam boolean descartar) {
        return ResponseEntity.ok(incidenteService.moderar(id, descartar));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        incidenteService.eliminar(id);
    }

    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<IncidenteDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(incidenteService.listarPorUsuario(id_usuario));
    }

    @GetMapping("/zona/{id_zona}")
    public ResponseEntity<List<IncidenteDTO>> listarPorZona(@PathVariable Long id_zona) {
        return ResponseEntity.ok(incidenteService.listarPorZona(id_zona));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<IncidenteDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(incidenteService.listarPorEstado(estado));
    }

    // HU-46: reportes denunciados como falsa alarma
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/denunciados")
    public ResponseEntity<List<IncidenteDTO>> listarDenunciados() {
        return ResponseEntity.ok(incidenteService.listarDenunciados());
    }

    // HU-20: incidentes cercanos (radio en metros)
    @GetMapping("/cercanos")
    public ResponseEntity<List<IncidenteDTO>> listarCercanos(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radio) {
        return ResponseEntity.ok(incidenteService.listarCercanos(lat, lng, radio));
    }

    // HU-38: incidentes filtrados para las estadísticas (todos los filtros son opcionales)
    @GetMapping("/filtrar")
    public ResponseEntity<List<IncidenteDTO>> listarFiltrados(
            @RequestParam(required = false) String distrito,
            @RequestParam(required = false) String tipologia,
            @RequestParam(required = false) Integer dias) {
        return ResponseEntity.ok(incidenteService.listarFiltrados(distrito, tipologia, dias));
    }
}
