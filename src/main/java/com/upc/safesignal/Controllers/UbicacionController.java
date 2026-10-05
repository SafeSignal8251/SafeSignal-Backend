package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    @GetMapping
    public ResponseEntity<List<UbicacionDTO>> listar() {
        return ResponseEntity.ok(ubicacionService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UbicacionDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.obtenerPorId(id));
    }

    // HU-10: registrar un punto de la ubicación en tiempo real
    @PostMapping
    public ResponseEntity<UbicacionDTO> registrar(
            @RequestParam Long id_seguimiento,
            @RequestBody UbicacionDTO ubicacionDTO) {
        return ResponseEntity.ok(ubicacionService.registrar(id_seguimiento, ubicacionDTO));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        ubicacionService.eliminar(id);
    }

    @GetMapping("/seguimiento/{id_seguimiento}")
    public ResponseEntity<List<UbicacionDTO>> listarPorSeguimiento(@PathVariable Long id_seguimiento) {
        return ResponseEntity.ok(ubicacionService.listarPorSeguimiento(id_seguimiento));
    }

    // HU-10: última ubicación compartida (la consulta el contacto de confianza)
    @GetMapping("/seguimiento/{id_seguimiento}/ultima")
    public ResponseEntity<UbicacionDTO> ultima(@PathVariable Long id_seguimiento) {
        return ResponseEntity.ok(ubicacionService.ultima(id_seguimiento));
    }
}
