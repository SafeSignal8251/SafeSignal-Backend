package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zonas-riesgo")
public class ZonaRiesgoController {

    @Autowired
    private ZonaRiesgoService zonaService;

    @GetMapping
    public ResponseEntity<List<ZonaRiesgoDTO>> listar() {
        return ResponseEntity.ok(zonaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZonaRiesgoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(zonaService.obtenerPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ZonaRiesgoDTO> registrar(@RequestBody ZonaRiesgoDTO zonaDTO) {
        return ResponseEntity.ok(zonaService.registrar(zonaDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ZonaRiesgoDTO> actualizar(@PathVariable Long id, @RequestBody ZonaRiesgoDTO zonaDTO) {
        return ResponseEntity.ok(zonaService.actualizar(id, zonaDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
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
    @PutMapping("/{id}/umbrales")
    public ResponseEntity<ZonaRiesgoDTO> configurarUmbrales(@PathVariable Long id, @RequestBody ZonaRiesgoDTO zonaDTO) {
        return ResponseEntity.ok(zonaService.configurarUmbrales(id, zonaDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/recalcular")
    public ResponseEntity<ZonaRiesgoDTO> recalcular(@PathVariable Long id) {
        return ResponseEntity.ok(zonaService.recalcularRiesgo(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/estado")
    public ResponseEntity<ZonaRiesgoDTO> cambiarEstado(@PathVariable Long id, @RequestParam Boolean estado) {
        return ResponseEntity.ok(zonaService.cambiarEstado(id, estado));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        zonaService.eliminar(id);
    }

    // HU-18 / HU-25: zonas activas para el mapa
    @GetMapping("/activas")
    public ResponseEntity<List<ZonaRiesgoDTO>> listarActivas() {
        return ResponseEntity.ok(zonaService.listarActivas());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ZonaRiesgoDTO>> listarPorEstado(@PathVariable Boolean estado) {
        return ResponseEntity.ok(zonaService.listarPorEstado(estado));
    }

    // HU-29: filtrar mapa por nivel de riesgo (BAJO, MEDIO, ALTO)
    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<List<ZonaRiesgoDTO>> listarPorNivel(@PathVariable String nivel) {
        return ResponseEntity.ok(zonaService.listarPorNivel(nivel));
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<ZonaRiesgoDTO>> buscarPorNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(zonaService.buscarPorNombre(nombre));
    }
}
