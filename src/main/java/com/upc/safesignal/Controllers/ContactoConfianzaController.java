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

@Tag(name = "Contactos de confianza", description = "Contactos que reciben los avisos cuando el usuario activa una alerta.")
@RestController
@RequestMapping("/api/contactos-confianza")
public class ContactoConfianzaController {

    @Autowired
    private ContactoConfianzaService contactoService;

    @Operation(summary = "Listar contactos de confianza", description = "Devuelve todos los registros de contactos de confianza.")
    @GetMapping
    public ResponseEntity<List<ContactoConfianzaDTO>> listar() {
        return ResponseEntity.ok(contactoService.listar());
    }

    @Operation(summary = "Obtener contacto de confianza por id", description = "Devuelve el registro de contacto de confianza con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<ContactoConfianzaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(contactoService.obtenerPorId(id));
    }

    // HU-06: registrar contacto de confianza
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Registrar un contacto de confianza", description = "Registra el contacto con sus nombres, teléfono, parentesco y prioridad. El teléfono no puede repetirse entre los contactos del mismo usuario.")
    @PostMapping
    public ResponseEntity<ContactoConfianzaDTO> registrar(
            @RequestParam Long id_usuario,
            @RequestBody ContactoConfianzaDTO contactoDTO) {
        return ResponseEntity.ok(contactoService.registrar(id_usuario, contactoDTO));
    }

    @Operation(summary = "Actualizar contacto de confianza", description = "Modifica los datos del registro de contacto de confianza con el id indicado; los campos que no se envían no se modifican.")
    @PutMapping("/{id}")
    public ResponseEntity<ContactoConfianzaDTO> actualizar(
            @PathVariable Long id,
            @RequestBody ContactoConfianzaDTO contactoDTO) {
        return ResponseEntity.ok(contactoService.actualizar(id, contactoDTO));
    }

    @Operation(summary = "Eliminar contacto de confianza", description = "Elimina el registro de contacto de confianza con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        contactoService.eliminar(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Listar contactos de confianza de un usuario", description = "Devuelve los registros de contactos de confianza del usuario indicado.")
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<ContactoConfianzaDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(contactoService.listarPorUsuario(id_usuario));
    }
}
