package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/validaciones-incidente")
public class ValidacionIncidenteController {

    @Autowired
    private ValidacionIncidenteService validacionService;

    @GetMapping
    public ResponseEntity<List<ValidacionIncidenteDTO>> listar() {
        return ResponseEntity.ok(validacionService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ValidacionIncidenteDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(validacionService.obtenerPorId(id));
    }

    // HU-40: tipo_voto = CONFIRMADO o FALSA_ALARMA
    @PostMapping
    public ResponseEntity<ValidacionIncidenteDTO> registrar(
            @RequestParam Long id_incidente,
            @RequestParam Long id_usuario,
            @RequestParam String tipo_voto) {
        return ResponseEntity.ok(validacionService.registrar(id_usuario, id_incidente, tipo_voto));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        validacionService.eliminar(id);
    }

    @GetMapping("/incidente/{id_incidente}")
    public ResponseEntity<List<ValidacionIncidenteDTO>> listarPorIncidente(@PathVariable Long id_incidente) {
        return ResponseEntity.ok(validacionService.listarPorIncidente(id_incidente));
    }

    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<ValidacionIncidenteDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(validacionService.listarPorUsuario(id_usuario));
    }

    // Cantidad de votos de un tipo en un incidente
    @GetMapping("/incidente/{id_incidente}/votos")
    public ResponseEntity<Long> contarVotos(@PathVariable Long id_incidente, @RequestParam String tipo_voto) {
        return ResponseEntity.ok(validacionService.contarVotos(id_incidente, tipo_voto));
    }
}
