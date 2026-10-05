package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaPublicadaController {

    @Autowired
    private EstadisticaPublicadaService estadisticaService;

    // HU-24: publicar estadística (body: titulo, descripcion, categoria, parametros)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EstadisticaPublicadaDTO> publicar(
            @RequestParam Long id_usuario,
            @RequestBody EstadisticaPublicadaDTO estadisticaDTO) {
        return ResponseEntity.ok(estadisticaService.publicar(id_usuario, estadisticaDTO));
    }

    // estadísticas publicadas, las más recientes primero
    @GetMapping
    public ResponseEntity<List<EstadisticaPublicadaDTO>> listar() {
        return ResponseEntity.ok(estadisticaService.listar());
    }

    // HU-14: dashboard del administrador (el distrito es opcional)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(
            @RequestParam Long id_usuario,
            @RequestParam(required = false) String distrito) {
        return ResponseEntity.ok(estadisticaService.dashboard(id_usuario, distrito));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadisticaPublicadaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(estadisticaService.obtenerPorId(id));
    }

    // HU-41: exportar reporte de estadísticas (CSV)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{id}/exportar", produces = "text/csv")
    public String exportar(@PathVariable Long id) {
        return estadisticaService.exportarCsv(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        estadisticaService.eliminar(id);
    }
}
