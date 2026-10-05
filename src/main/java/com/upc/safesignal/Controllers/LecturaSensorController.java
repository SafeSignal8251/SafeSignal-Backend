package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecturas-sensor")
public class LecturaSensorController {

    @Autowired
    private LecturaSensorService lecturaService;

    @GetMapping
    public ResponseEntity<List<LecturaSensorDTO>> listar() {
        return ResponseEntity.ok(lecturaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LecturaSensorDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(lecturaService.obtenerPorId(id));
    }

    // HU-15: si la lectura trae anomalia = true se genera una alerta automática
    @PostMapping
    public ResponseEntity<LecturaSensorDTO> registrar(
            @RequestParam Long id_dispositivo,
            @RequestBody LecturaSensorDTO lecturaDTO) {
        return ResponseEntity.ok(lecturaService.registrar(id_dispositivo, lecturaDTO));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        lecturaService.eliminar(id);
    }

    @GetMapping("/dispositivo/{id_dispositivo}")
    public ResponseEntity<List<LecturaSensorDTO>> listarPorDispositivo(@PathVariable Long id_dispositivo) {
        return ResponseEntity.ok(lecturaService.listarPorDispositivo(id_dispositivo));
    }

    @GetMapping("/anomalias")
    public ResponseEntity<List<LecturaSensorDTO>> listarAnomalias() {
        return ResponseEntity.ok(lecturaService.listarAnomalias());
    }

    @GetMapping("/dispositivo/{id_dispositivo}/anomalias")
    public ResponseEntity<List<LecturaSensorDTO>> listarAnomaliasPorDispositivo(@PathVariable Long id_dispositivo) {
        return ResponseEntity.ok(lecturaService.listarAnomaliasPorDispositivo(id_dispositivo));
    }
}
