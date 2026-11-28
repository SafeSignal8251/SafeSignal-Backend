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

@Tag(name = "Zonas de riesgo", description = "Zonas de riesgo con su nivel (BAJO, MEDIO o ALTO), umbrales y reclasificación.")
@RestController
@RequestMapping("/api/zonas-riesgo")
public class ZonaRiesgoController {

    @Autowired
    private ZonaRiesgoService zonaService;

    @Operation(summary = "Listar zonas de riesgo", description = "Devuelve todos los registros de zonas de riesgo.")
    @GetMapping
    public ResponseEntity<List<ZonaRiesgoDTO>> listar() {
        return ResponseEntity.ok(zonaService.listar());
    }

    @Operation(summary = "Obtener zona de riesgo por id", description = "Devuelve el registro de zona de riesgo con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<ZonaRiesgoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(zonaService.obtenerPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una zona de riesgo (administrador)", description = "Registra la zona con su nombre, nivel y ubicación. Solo un ADMIN puede hacerlo.")
    @PostMapping
    public ResponseEntity<ZonaRiesgoDTO> registrar(@RequestBody ZonaRiesgoDTO zonaDTO) {
        return ResponseEntity.ok(zonaService.registrar(zonaDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una zona de riesgo (administrador)", description = "Modifica los datos de la zona.")
    @PutMapping("/{id}")
    public ResponseEntity<ZonaRiesgoDTO> actualizar(@PathVariable Long id, @RequestBody ZonaRiesgoDTO zonaDTO) {
        return ResponseEntity.ok(zonaService.actualizar(id, zonaDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar el nivel de riesgo de una zona (administrador)", description = "Fija el nivel BAJO, MEDIO o ALTO de la zona.")
    @PutMapping("/{id}/riesgo")
    public ResponseEntity<ZonaRiesgoDTO> configurarRiesgo(
            @PathVariable Long id,
            @RequestParam String nivel_riesgo,
            @RequestParam Double puntaje_riesgo) {

        return ResponseEntity.ok(zonaService.configurarRiesgo(id, nivel_riesgo, puntaje_riesgo));
    }

    // HU-43: configurar umbrales de la zona
    // body: umbral_alto_min, umbral_medio_min, umbral_medio_max, umbral_bajo_max, ventana_dias, radio_geocerca
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Configurar los umbrales de una zona (administrador)", description = "Guarda los umbrales y la ventana de días. Valida que bajo_max < medio_min <= medio_max < alto_min y reclasifica la zona según la densidad de incidentes por km².")
    @PutMapping("/{id}/umbrales")
    public ResponseEntity<ZonaRiesgoDTO> configurarUmbrales(@PathVariable Long id, @RequestBody ZonaRiesgoDTO zonaDTO) {
        return ResponseEntity.ok(zonaService.configurarUmbrales(id, zonaDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recalcular el nivel de una zona (administrador)", description = "Reclasifica la zona con los incidentes de su ventana de días.")
    @PutMapping("/{id}/recalcular")
    public ResponseEntity<ZonaRiesgoDTO> recalcular(@PathVariable Long id) {
        return ResponseEntity.ok(zonaService.recalcularRiesgo(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar o desactivar una zona (administrador)", description = "Cambia si la zona está activa (true) o inactiva (false).")
    @PutMapping("/{id}/estado")
    public ResponseEntity<ZonaRiesgoDTO> cambiarEstado(@PathVariable Long id, @RequestParam Boolean estado) {
        return ResponseEntity.ok(zonaService.cambiarEstado(id, estado));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar zona de riesgo", description = "Elimina el registro de zona de riesgo con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        zonaService.eliminar(id);
    }

    // HU-18 / HU-25: zonas activas para el mapa
    @Operation(summary = "Listar las zonas de riesgo activas", description = "Devuelve las zonas activas con su nivel (BAJO, MEDIO o ALTO), para dibujarlas en el mapa.")
    @GetMapping("/activas")
    public ResponseEntity<List<ZonaRiesgoDTO>> listarActivas() {
        return ResponseEntity.ok(zonaService.listarActivas());
    }

    @Operation(summary = "Listar zonas por estado", description = "Devuelve las zonas activas (true) o inactivas (false).")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ZonaRiesgoDTO>> listarPorEstado(@PathVariable Boolean estado) {
        return ResponseEntity.ok(zonaService.listarPorEstado(estado));
    }

    // HU-29: filtrar mapa por nivel de riesgo (BAJO, MEDIO, ALTO)
    @Operation(summary = "Filtrar zonas por nivel de riesgo", description = "Devuelve las zonas con el nivel indicado: BAJO, MEDIO o ALTO.")
    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<List<ZonaRiesgoDTO>> listarPorNivel(@PathVariable String nivel) {
        return ResponseEntity.ok(zonaService.listarPorNivel(nivel));
    }

    @Operation(summary = "Buscar zonas por nombre", description = "Devuelve las zonas cuyo nombre contiene el texto indicado, sin distinguir mayúsculas.")
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<ZonaRiesgoDTO>> buscarPorNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(zonaService.buscarPorNombre(nombre));
    }
}
