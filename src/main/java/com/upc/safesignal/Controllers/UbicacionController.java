package com.upc.safesignal.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Ubicaciones", description = "Puntos de ubicación registrados durante un seguimiento.")
@RestController
@RequestMapping("/api/ubicaciones")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    @Operation(summary = "Listar ubicaciones", description = "Devuelve todos los registros de ubicaciones.")
    @GetMapping
    public ResponseEntity<List<UbicacionDTO>> listar() {
        return ResponseEntity.ok(ubicacionService.listar());
    }

    @Operation(summary = "Obtener ubicación por id", description = "Devuelve el registro de ubicación con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<UbicacionDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.obtenerPorId(id));
    }

    // HU-10: registrar un punto de la ubicación en tiempo real
    @Operation(summary = "Registrar un punto de ubicación", description = "Guarda un punto (latitud, longitud y precisión) mientras el seguimiento está ACTIVO.")
    @PostMapping
    public ResponseEntity<UbicacionDTO> registrar(
            @RequestParam Long id_seguimiento,
            @RequestBody UbicacionDTO ubicacionDTO) {
        return ResponseEntity.ok(ubicacionService.registrar(id_seguimiento, ubicacionDTO));
    }

    @Operation(summary = "Eliminar ubicación", description = "Elimina el registro de ubicación con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        ubicacionService.eliminar(id);
    }

    @Operation(summary = "Listar los puntos de un seguimiento", description = "Devuelve los puntos registrados, para dibujar el recorrido en el mapa.")
    @GetMapping("/seguimiento/{id_seguimiento}")
    public ResponseEntity<List<UbicacionDTO>> listarPorSeguimiento(@PathVariable Long id_seguimiento) {
        return ResponseEntity.ok(ubicacionService.listarPorSeguimiento(id_seguimiento));
    }

    // HU-10: última ubicación compartida (la consulta el contacto de confianza)
    @Operation(summary = "Última ubicación de un seguimiento", description = "Devuelve el punto más reciente del seguimiento.")
    @GetMapping("/seguimiento/{id_seguimiento}/ultima")
    public ResponseEntity<UbicacionDTO> ultima(@PathVariable Long id_seguimiento) {
        return ResponseEntity.ok(ubicacionService.ultima(id_seguimiento));
    }
}
