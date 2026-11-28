package com.upc.safesignal.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Estadísticas", description = "Estadísticas publicadas por el administrador y panel de control.")
@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaPublicadaController {

    @Autowired
    private EstadisticaPublicadaService estadisticaService;

    // HU-24: publicar estadística (body: titulo, descripcion, categoria, parametros)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Publicar una estadística (administrador)", description = "Publica la estadística y genera su código con el formato REP-año-n. Solo un usuario con rol ADMIN puede hacerlo.")
    @PostMapping
    public ResponseEntity<EstadisticaPublicadaDTO> publicar(
            @RequestParam Long id_usuario,
            @RequestBody EstadisticaPublicadaDTO estadisticaDTO) {
        return ResponseEntity.ok(estadisticaService.publicar(id_usuario, estadisticaDTO));
    }

    // estadísticas publicadas, las más recientes primero
    @Operation(summary = "Listar estadísticas publicadas", description = "Devuelve todos los registros de estadísticas publicadas.")
    @GetMapping
    public ResponseEntity<List<EstadisticaPublicadaDTO>> listar() {
        return ResponseEntity.ok(estadisticaService.listar());
    }

    // HU-14: dashboard del administrador (el distrito es opcional)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Contadores del panel del administrador", description = "Devuelve el total de usuarios, incidentes (total, pendientes y verificados), alertas activas y zonas de riesgo alto. El distrito es opcional.")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(
            @RequestParam Long id_usuario,
            @RequestParam(required = false) String distrito) {
        return ResponseEntity.ok(estadisticaService.dashboard(id_usuario, distrito));
    }

    @Operation(summary = "Obtener estadística publicada por id", description = "Devuelve el registro de estadística publicada con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<EstadisticaPublicadaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(estadisticaService.obtenerPorId(id));
    }

    // HU-41: exportar reporte de estadísticas (CSV)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exportar una estadística en CSV (administrador)", description = "Devuelve en formato CSV el total de incidentes por distrito y tipo, sin contar los ELIMINADOS.")
    @GetMapping(value = "/{id}/exportar", produces = "text/csv")
    public String exportar(@PathVariable Long id) {
        return estadisticaService.exportarCsv(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar estadística publicada", description = "Elimina el registro de estadística publicada con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        estadisticaService.eliminar(id);
    }
}
