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

@Tag(name = "Comentarios", description = "Comentarios de los vecinos en los reportes comunitarios.")
@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @Operation(summary = "Listar comentarios", description = "Devuelve todos los registros de comentarios.")
    @GetMapping
    public ResponseEntity<List<ComentarioDTO>> listar() {
        return ResponseEntity.ok(comentarioService.listar());
    }

    @Operation(summary = "Obtener comentario por id", description = "Devuelve el registro de comentario con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<ComentarioDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(comentarioService.obtenerPorId(id));
    }

    // HU-35: comentar en un reporte comunitario
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Comentar en un reporte comunitario", description = "Registra el comentario del usuario en el incidente. El texto es obligatorio y admite hasta 500 caracteres.")
    @PostMapping
    public ResponseEntity<ComentarioDTO> registrar(
            @RequestParam Long id_usuario,
            @RequestParam Long id_incidente,
            @RequestBody ComentarioDTO comentarioDTO) {
        return ResponseEntity.ok(comentarioService.registrar(id_usuario, id_incidente, comentarioDTO));
    }

    @Operation(summary = "Actualizar comentario", description = "Modifica los datos del registro de comentario con el id indicado; los campos que no se envían no se modifican.")
    @PutMapping("/{id}")
    public ResponseEntity<ComentarioDTO> actualizar(@PathVariable Long id, @RequestParam String texto) {
        return ResponseEntity.ok(comentarioService.actualizar(id, texto));
    }

    @Operation(summary = "Eliminar comentario", description = "Elimina el registro de comentario con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        comentarioService.eliminar(id);
    }

    @Operation(summary = "Listar los comentarios de un reporte", description = "Devuelve los comentarios del incidente indicado.")
    @GetMapping("/incidente/{id_incidente}")
    public ResponseEntity<List<ComentarioDTO>> listarPorIncidente(@PathVariable Long id_incidente) {
        return ResponseEntity.ok(comentarioService.listarPorIncidente(id_incidente));
    }

    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Listar comentarios de un usuario", description = "Devuelve los registros de comentarios del usuario indicado.")
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<ComentarioDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(comentarioService.listarPorUsuario(id_usuario));
    }
}
