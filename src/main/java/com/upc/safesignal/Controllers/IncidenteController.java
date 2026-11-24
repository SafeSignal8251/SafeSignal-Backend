package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/incidentes")
public class IncidenteController {

    @Autowired
    private IncidenteService incidenteService;

    // HU-46 / HU-39: un reporte ELIMINADO por el administrador deja de ser visible para los clientes
    private boolean esAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private List<IncidenteDTO> visibles(List<IncidenteDTO> lista, Authentication auth) {
        if (esAdmin(auth)) {
            return lista;
        }
        return lista.stream().filter(i -> !"ELIMINADO".equals(i.getEstado())).collect(Collectors.toList());
    }

    @GetMapping
    public ResponseEntity<List<IncidenteDTO>> listar(Authentication auth) {
        return ResponseEntity.ok(visibles(incidenteService.listar(), auth));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidenteDTO> obtener(@PathVariable Long id, Authentication auth) {
        IncidenteDTO dto = incidenteService.obtenerPorId(id);
        if (!esAdmin(auth) && "ELIMINADO".equals(dto.getEstado())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    // HU-16 / HU-22: registrar incidente
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @PostMapping
    public ResponseEntity<IncidenteDTO> registrar(
            @RequestParam(required = false) Long id_zona,
            @RequestParam Long id_usuario,
            @RequestBody IncidenteDTO incidenteDTO) {
        return ResponseEntity.ok(incidenteService.registrar(id_usuario, id_zona, incidenteDTO));
    }

    // HU-16: reporte de incidente post-emergencia
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @PostMapping("/post-emergencia")
    public ResponseEntity<IncidenteDTO> registrarPostEmergencia(
            @RequestParam Long id_usuario,
            @RequestParam Long id_alerta,
            @RequestParam String descripcion) {
        return ResponseEntity.ok(incidenteService.registrarPostEmergencia(id_usuario, id_alerta, descripcion));
    }

    // HU-37: editar un reporte propio (body: descripcion)
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @PutMapping("/{id}")
    public ResponseEntity<IncidenteDTO> actualizar(
            @PathVariable Long id,
            @RequestParam Long id_usuario,
            @RequestBody IncidenteDTO incidenteDTO) {
        return ResponseEntity.ok(incidenteService.actualizarDescripcion(id, id_usuario, incidenteDTO.getDescripcion()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/estado")
    public ResponseEntity<IncidenteDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(incidenteService.cambiarEstado(id, estado));
    }

    // HU-39 / HU-46: descartar (descartar=true) o mantener (descartar=false)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/moderar")
    public ResponseEntity<IncidenteDTO> moderar(@PathVariable Long id, @RequestParam boolean descartar) {
        return ResponseEntity.ok(incidenteService.moderar(id, descartar));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        incidenteService.eliminar(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<IncidenteDTO>> listarPorUsuario(@PathVariable Long id_usuario, Authentication auth) {
        return ResponseEntity.ok(visibles(incidenteService.listarPorUsuario(id_usuario), auth));
    }

    @GetMapping("/zona/{id_zona}")
    public ResponseEntity<List<IncidenteDTO>> listarPorZona(@PathVariable Long id_zona, Authentication auth) {
        return ResponseEntity.ok(visibles(incidenteService.listarPorZona(id_zona), auth));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<IncidenteDTO>> listarPorEstado(@PathVariable String estado, Authentication auth) {
        return ResponseEntity.ok(visibles(incidenteService.listarPorEstado(estado), auth));
    }

    // HU-46: reportes denunciados como falsa alarma
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/denunciados")
    public ResponseEntity<List<IncidenteDTO>> listarDenunciados() {
        return ResponseEntity.ok(incidenteService.listarDenunciados());
    }

    // HU-20: incidentes cercanos (radio en metros)
    @GetMapping("/cercanos")
    public ResponseEntity<List<IncidenteDTO>> listarCercanos(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radio,
            Authentication auth) {
        return ResponseEntity.ok(visibles(incidenteService.listarCercanos(lat, lng, radio), auth));
    }

    // HU-38: incidentes filtrados para las estadísticas (todos los filtros son opcionales)
    @GetMapping("/filtrar")
    public ResponseEntity<List<IncidenteDTO>> listarFiltrados(
            @RequestParam(required = false) String distrito,
            @RequestParam(required = false) String tipologia,
            @RequestParam(required = false) Integer dias) {
        return ResponseEntity.ok(incidenteService.listarFiltrados(distrito, tipologia, dias));
    }
}
