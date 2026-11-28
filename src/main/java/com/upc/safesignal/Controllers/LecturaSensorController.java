package com.upc.safesignal.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Lecturas de sensor", description = "Lecturas enviadas por los dispositivos IoT; una lectura anómala genera una alerta automática.")
@RestController
@RequestMapping("/api/lecturas-sensor")
public class LecturaSensorController {

    @Autowired
    private LecturaSensorService lecturaService;

    @Operation(summary = "Listar lecturas de sensor", description = "Devuelve todos los registros de lecturas de sensor.")
    @GetMapping
    public ResponseEntity<List<LecturaSensorDTO>> listar() {
        return ResponseEntity.ok(lecturaService.listar());
    }

    @Operation(summary = "Obtener lectura de sensor por id", description = "Devuelve el registro de lectura de sensor con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<LecturaSensorDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(lecturaService.obtenerPorId(id));
    }

    // HU-15: si la lectura trae anomalia = true se genera una alerta automática
    @Operation(summary = "Registrar una lectura de sensor", description = "Guarda la lectura. Si anomalia es true, se genera automáticamente una alerta de tipo ANOMALIA y se notifica a los contactos del dueño del dispositivo.")
    @PostMapping
    public ResponseEntity<LecturaSensorDTO> registrar(
            @RequestParam Long id_dispositivo,
            @RequestBody LecturaSensorDTO lecturaDTO) {
        return ResponseEntity.ok(lecturaService.registrar(id_dispositivo, lecturaDTO));
    }

    @Operation(summary = "Eliminar lectura de sensor", description = "Elimina el registro de lectura de sensor con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        lecturaService.eliminar(id);
    }

    @Operation(summary = "Listar las lecturas de un dispositivo", description = "Devuelve las lecturas registradas por el dispositivo.")
    @GetMapping("/dispositivo/{id_dispositivo}")
    public ResponseEntity<List<LecturaSensorDTO>> listarPorDispositivo(@PathVariable Long id_dispositivo) {
        return ResponseEntity.ok(lecturaService.listarPorDispositivo(id_dispositivo));
    }

    @Operation(summary = "Listar todas las lecturas anómalas", description = "Devuelve las lecturas marcadas como anomalía.")
    @GetMapping("/anomalias")
    public ResponseEntity<List<LecturaSensorDTO>> listarAnomalias() {
        return ResponseEntity.ok(lecturaService.listarAnomalias());
    }

    @Operation(summary = "Listar las lecturas anómalas de un dispositivo", description = "Devuelve las lecturas marcadas como anomalía del dispositivo indicado.")
    @GetMapping("/dispositivo/{id_dispositivo}/anomalias")
    public ResponseEntity<List<LecturaSensorDTO>> listarAnomaliasPorDispositivo(@PathVariable Long id_dispositivo) {
        return ResponseEntity.ok(lecturaService.listarAnomaliasPorDispositivo(id_dispositivo));
    }
}
