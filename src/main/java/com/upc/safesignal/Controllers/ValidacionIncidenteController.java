package com.upc.safesignal.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Validaciones de incidentes", description = "Votos de los vecinos sobre un reporte (confirmado o falsa alarma).")
@RestController
@RequestMapping("/api/validaciones-incidente")
public class ValidacionIncidenteController {

    @Autowired
    private ValidacionIncidenteService validacionService;

    @Operation(summary = "Listar validaciones de incidentes", description = "Devuelve todos los registros de validaciones de incidentes.")
    @GetMapping
    public ResponseEntity<List<ValidacionIncidenteDTO>> listar() {
        return ResponseEntity.ok(validacionService.listar());
    }

    @Operation(summary = "Obtener validación de incidente por id", description = "Devuelve el registro de validación de incidente con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<ValidacionIncidenteDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(validacionService.obtenerPorId(id));
    }

    // HU-40: tipo_voto = CONFIRMADO o FALSA_ALARMA
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Votar sobre un reporte", description = "Registra el voto: tipo_voto CONFIRMADO o FALSA_ALARMA. Un usuario solo puede votar una vez por reporte y no puede validar el suyo.")
    @PostMapping
    public ResponseEntity<ValidacionIncidenteDTO> registrar(
            @RequestParam Long id_incidente,
            @RequestParam Long id_usuario,
            @RequestParam String tipo_voto) {
        return ResponseEntity.ok(validacionService.registrar(id_usuario, id_incidente, tipo_voto));
    }

    @Operation(summary = "Eliminar validación de incidente", description = "Elimina el registro de validación de incidente con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        validacionService.eliminar(id);
    }

    @Operation(summary = "Listar los votos de un reporte", description = "Devuelve los votos registrados sobre el incidente.")
    @GetMapping("/incidente/{id_incidente}")
    public ResponseEntity<List<ValidacionIncidenteDTO>> listarPorIncidente(@PathVariable Long id_incidente) {
        return ResponseEntity.ok(validacionService.listarPorIncidente(id_incidente));
    }

    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Listar validaciones de incidentes de un usuario", description = "Devuelve los registros de validaciones de incidentes del usuario indicado.")
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<ValidacionIncidenteDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(validacionService.listarPorUsuario(id_usuario));
    }

    // Cantidad de votos de un tipo en un incidente
    @Operation(summary = "Contar los votos de un reporte", description = "Devuelve el número de votos del tipo indicado (CONFIRMADO o FALSA_ALARMA).")
    @GetMapping("/incidente/{id_incidente}/votos")
    public ResponseEntity<Long> contarVotos(@PathVariable Long id_incidente, @RequestParam String tipo_voto) {
        return ResponseEntity.ok(validacionService.contarVotos(id_incidente, tipo_voto));
    }
}
