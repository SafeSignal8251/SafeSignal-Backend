package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @GetMapping
    public ResponseEntity<List<ComentarioDTO>> listar() {
        return ResponseEntity.ok(comentarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComentarioDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(comentarioService.obtenerPorId(id));
    }

    // HU-35: comentar en un reporte comunitario
    @PostMapping
    public ResponseEntity<ComentarioDTO> registrar(
            @RequestParam Long id_usuario,
            @RequestParam Long id_incidente,
            @RequestBody ComentarioDTO comentarioDTO) {
        return ResponseEntity.ok(comentarioService.registrar(id_usuario, id_incidente, comentarioDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioDTO> actualizar(@PathVariable Long id, @RequestParam String texto) {
        return ResponseEntity.ok(comentarioService.actualizar(id, texto));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        comentarioService.eliminar(id);
    }

    @GetMapping("/incidente/{id_incidente}")
    public ResponseEntity<List<ComentarioDTO>> listarPorIncidente(@PathVariable Long id_incidente) {
        return ResponseEntity.ok(comentarioService.listarPorIncidente(id_incidente));
    }

    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<ComentarioDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(comentarioService.listarPorUsuario(id_usuario));
    }
}
