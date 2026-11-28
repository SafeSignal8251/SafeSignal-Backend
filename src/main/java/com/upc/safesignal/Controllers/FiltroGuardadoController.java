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

@Tag(name = "Filtros guardados", description = "Filtros de la pantalla de estadísticas que el usuario guarda como favoritos.")
@RestController
@RequestMapping("/api/filtros-guardados")
public class FiltroGuardadoController {

    @Autowired
    private FiltroGuardadoService filtroService;

    // HU-42 / HU-44: guardar filtro (body: nombre, distrito, tipologia y rango_dias)
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Guardar un filtro de estadísticas", description = "Guarda la combinación de distrito, tipología y rango de días. No permite repetir el nombre para el mismo usuario.")
    @PostMapping
    public ResponseEntity<FiltroGuardadoDTO> registrar(
            @RequestParam Long id_usuario,
            @RequestBody FiltroGuardadoDTO filtroDTO) {
        return ResponseEntity.ok(filtroService.registrar(id_usuario, filtroDTO));
    }

    @Operation(summary = "Obtener filtro guardado por id", description = "Devuelve el registro de filtro guardado con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<FiltroGuardadoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(filtroService.obtenerPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Listar filtros guardados de un usuario", description = "Devuelve los registros de filtros guardados del usuario indicado.")
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<FiltroGuardadoDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(filtroService.listarPorUsuario(id_usuario));
    }

    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Eliminar filtro guardado", description = "Elimina el registro de filtro guardado con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id, @RequestParam Long id_usuario) {
        filtroService.eliminar(id, id_usuario);
    }
}
