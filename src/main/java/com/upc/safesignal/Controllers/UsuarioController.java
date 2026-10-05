package com.upc.safesignal.Controllers;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    // HU-48: listar cuentas por estado (ACTIVA o SUSPENDIDA)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<UsuarioDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(usuarioService.listarPorEstado(estado));
    }

    @GetMapping("/existe-correo/{correo}")
    public boolean existeCorreo(@PathVariable String correo) {
        return usuarioService.existeCorreo(correo);
    }

    @GetMapping("/existe-dni/{dni}")
    public boolean existeDni(@PathVariable String dni) {
        return usuarioService.existeDni(dni);
    }

    // HU-01: crear cuenta
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.registrar(usuarioDTO));
    }

    // HU-01: solo un administrador crea otros administradores
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/registrar-admin")
    public ResponseEntity<UsuarioDTO> registrarAdmin(@RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.registrarAdmin(usuarioDTO));
    }

    // HU-02: iniciar sesión (body: correo, contrasena y, opcionales, dni y rol)
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody UsuarioDTO datos) {
        return ResponseEntity.ok(usuarioService.iniciarSesion(
                datos.getCorreo(),
                datos.getDni(),
                datos.getContrasena(),
                datos.getRol()));
    }

    // HU-05: cerrar sesión
    @PostMapping("/logout")
    public String logout() {
        return "Sesión cerrada correctamente";
    }

    // HU-03 paso 1 (body: correo y dni)
    @PostMapping("/recuperar-password")
    public String recuperarPassword(@RequestBody UsuarioDTO datos) {
        usuarioService.verificarCuenta(datos.getCorreo(), datos.getDni());
        return "Cuenta verificada. Ya puedes restablecer tu contraseña";
    }

    // HU-03 paso 2 (body: correo, dni y contrasena nueva)
    @PutMapping("/restablecer-password")
    public String restablecerPassword(@RequestBody UsuarioDTO datos) {
        usuarioService.restablecerContrasena(datos.getCorreo(), datos.getDni(), datos.getContrasena());
        return "Contraseña actualizada correctamente";
    }

    // HU-04: editar perfil
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.actualizar(id, usuarioDTO));
    }

    // HU-49: personalizar mensaje predeterminado de alerta SOS
    @PutMapping("/{id}/mensaje-sos")
    public ResponseEntity<UsuarioDTO> actualizarMensajeSos(@PathVariable Long id, @RequestParam String mensaje) {
        return ResponseEntity.ok(usuarioService.actualizarMensajeSos(id, mensaje));
    }

    // HU-47: accesos rápidos de la pantalla principal
    @GetMapping("/{id}/accesos-rapidos")
    public ResponseEntity<Map<String, Object>> accesosRapidos(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.accesosRapidos(id));
    }

    // HU-48: suspender cuenta
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/suspender")
    public ResponseEntity<UsuarioDTO> suspender(@PathVariable Long id, @RequestParam String motivo) {
        return ResponseEntity.ok(usuarioService.suspender(id, motivo));
    }

    // HU-48: reactivar cuenta
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<UsuarioDTO> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.reactivar(id));
    }

    // HU-07: eliminar cuenta
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }
}
