package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filtros-guardados")
public class FiltroGuardadoController {

    @Autowired
    private FiltroGuardadoService filtroService;

    // HU-42 / HU-44: guardar filtro (body: nombre, distrito, tipologia y rango_dias)
    @PostMapping
    public ResponseEntity<FiltroGuardadoDTO> registrar(
            @RequestParam Long id_usuario,
            @RequestBody FiltroGuardadoDTO filtroDTO) {
        return ResponseEntity.ok(filtroService.registrar(id_usuario, filtroDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FiltroGuardadoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(filtroService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<FiltroGuardadoDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(filtroService.listarPorUsuario(id_usuario));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id, @RequestParam Long id_usuario) {
        filtroService.eliminar(id, id_usuario);
    }
}
