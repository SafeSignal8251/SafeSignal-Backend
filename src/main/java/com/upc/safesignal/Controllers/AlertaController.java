package com.upc.safesignal.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Alertas", description = "Alertas de emergencia (SOS, silenciosa y automática por anomalía): activación, cancelación, despacho e historial.")
@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    @Autowired
    private AlertaService alertaService;

    @Operation(summary = "Listar alertas", description = "Devuelve todos los registros de alertas.")
    @GetMapping
    public ResponseEntity<List<AlertaDTO>> listar() {
        return ResponseEntity.ok(alertaService.listar());
    }

    @Operation(summary = "Obtener alerta por id", description = "Devuelve el registro de alerta con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<AlertaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.obtenerPorId(id));
    }

    // HU-08 / HU-09: alerta SOS o silenciosa (body: latitud, longitud y tipo_alerta SOS o SILENCIOSA)
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Activar una alerta SOS o silenciosa", description = "Registra la alerta con estado ACTIVA (tipo_alerta SOS o SILENCIOSA). La ubicación es obligatoria. Genera una notificación por cada contacto de confianza y una para el propio usuario.")
    @PostMapping
    public ResponseEntity<AlertaDTO> registrar(@RequestParam Long id_usuario, @RequestBody AlertaDTO alertaDTO) {
        return ResponseEntity.ok(alertaService.registrar(id_usuario, alertaDTO));
    }

    // HU-15: alerta automática de un dispositivo IoT
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Registrar una alerta originada por un dispositivo IoT", description = "Crea una alerta automática de tipo ANOMALIA con origen DISPOSITIVO. Si el cuerpo no trae coordenadas, usa la última ubicación conocida del usuario.")
    @PostMapping("/dispositivo")
    public ResponseEntity<AlertaDTO> registrarConDispositivo(
            @RequestParam Long id_usuario,
            @RequestParam Long id_dispositivo,
            @RequestBody AlertaDTO alertaDTO) {
        return ResponseEntity.ok(alertaService.registrarConDispositivo(id_usuario, id_dispositivo, alertaDTO));
    }

    // HU-11: cancelar alerta
    @Operation(summary = "Cancelar una alerta activa", description = "Cambia el estado a CANCELADA y guarda la fecha y hora de cancelación. Solo se puede cancelar una alerta que siga ACTIVA.")
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<AlertaDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.cancelar(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Despachar una alerta (administrador)", description = "Marca la alerta como DESPACHADA y guarda el código de despacho. Solo un ADMIN puede hacerlo y la alerta debe estar ACTIVA.")
    @PutMapping("/{id}/despachar")
    public ResponseEntity<AlertaDTO> despachar(@PathVariable Long id, @RequestParam String codigo_despacho) {
        return ResponseEntity.ok(alertaService.despachar(id, codigo_despacho));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar el estado de una alerta (administrador)", description = "Cambia el estado a ACTIVA, CANCELADA o DESPACHADA. Solo un ADMIN puede hacerlo.")
    @PutMapping("/{id}/estado")
    public ResponseEntity<AlertaDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(alertaService.cambiarEstado(id, estado));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar alerta", description = "Elimina el registro de alerta con el id indicado.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        alertaService.eliminar(id);
    }

    // HU-12: historial de alertas
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Historial de alertas de un usuario", description = "Devuelve las alertas del usuario, de la más reciente a la más antigua.")
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<List<AlertaDTO>> listarPorUsuario(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(alertaService.listarPorUsuario(id_usuario));
    }

    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
    @Operation(summary = "Historial de alertas de un usuario por estado", description = "Filtra las alertas del usuario por estado: ACTIVA, CANCELADA o DESPACHADA.")
    @GetMapping("/usuario/{id_usuario}/estado/{estado}")
    public ResponseEntity<List<AlertaDTO>> listarPorUsuarioYEstado(
            @PathVariable Long id_usuario,
            @PathVariable String estado) {
        return ResponseEntity.ok(alertaService.listarPorUsuarioYEstado(id_usuario, estado));
    }

    @Operation(summary = "Listar alertas por estado", description = "Devuelve las alertas con el estado indicado; el panel del administrador consulta las ACTIVAS para su cola de despacho.")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<AlertaDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(alertaService.listarPorEstado(estado));
    }
}
