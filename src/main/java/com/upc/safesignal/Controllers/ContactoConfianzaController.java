package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contactos-confianza")
public class ContactoConfianzaController {

    @Autowired
    private ContactoConfianzaService contactoService;

    @GetMapping
    public ResponseEntity<List<ContactoConfianzaDTO>> listar() {
        return ResponseEntity.ok(contactoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactoConfianzaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(contactoService.obtenerPorId(id));
    }

    // HU-06: registrar contacto de confianza
    @PostMapping
    public ResponseEntity<ContactoConfianzaDTO> registrar(
            @RequestParam Long id_usuario,
            @RequestBody ContactoConfianzaDTO contactoDTO) {
        return ResponseEntity.ok(contactoService.registrar(id_usuario, contactoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactoConfianzaDTO> actualizar(
            @PathVariable Long id,
            @RequestBody ContactoConfianzaDTO contactoDTO) {
        return ResponseEntity.ok(contactoService.actualizar(id, contactoDTO));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        contactoService.eliminar(id);
    }

    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<ContactoConfianzaDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(contactoService.listarPorUsuario(id_usuario));
    }
}
