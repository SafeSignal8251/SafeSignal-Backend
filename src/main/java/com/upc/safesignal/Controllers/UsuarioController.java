package com.upc.safesignal.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Services.*;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

@Tag(name = "Usuarios", description = "Cuentas de usuario: registro, inicio de sesión, perfil, recuperación de contraseña y gestión del administrador.")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ModelMapper modelMapper;

    // Código que entrega la institución para poder crear cuentas de administrador (ver application.properties)
    @Value("${safesignal.admin.codigo-registro:}")
    private String codigoRegistroAdmin;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar las cuentas (administrador)", description = "Devuelve todas las cuentas registradas. Solo un ADMIN puede consultarlo.")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id)")
    @Operation(summary = "Obtener usuario por id", description = "Devuelve el registro de usuario con el id indicado; responde con error si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    // HU-48: listar cuentas por estado (ACTIVA o SUSPENDIDA)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar cuentas por estado (administrador)", description = "Devuelve las cuentas ACTIVA o SUSPENDIDA.")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<UsuarioDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(usuarioService.listarPorEstado(estado));
    }

    @Operation(summary = "Verificar si un correo ya está registrado", description = "Devuelve true si ya existe una cuenta con ese correo. Endpoint público.")
    @GetMapping("/existe-correo/{correo}")
    public boolean existeCorreo(@PathVariable String correo) {
        return usuarioService.existeCorreo(correo);
    }

    @Operation(summary = "Verificar si un DNI ya está registrado", description = "Devuelve true si ya existe una cuenta con ese DNI. Endpoint público.")
    @GetMapping("/existe-dni/{dni}")
    public boolean existeDni(@PathVariable String dni) {
        return usuarioService.existeDni(dni);
    }

    // HU-01: crear cuenta
    @Operation(summary = "Registrar un cliente", description = "Valida los campos obligatorios, que la contraseña tenga al menos 8 caracteres y que el correo y el DNI no existan. Crea la cuenta ACTIVA con rol CLIENTE. Endpoint público.")
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody RegistroUsuarioDTO registro) {
        return ResponseEntity.ok(usuarioService.registrar(modelMapper.map(registro, UsuarioDTO.class)));
    }

    // HU-01 (escenario 2): cuenta de administrador desde la pantalla de registro. El código de placa / Id operativo
    // es el identificador de inicio de sesión (el campo DNI del login) y el código de autorización lo entrega la institución.
    @Operation(summary = "Registrar una cuenta institucional", description = "Registra una cuenta institucional con su código de placa o id operativo. Endpoint público.")
    @PostMapping("/registrar-institucional")
    public ResponseEntity<?> registrarInstitucional(@Valid @RequestBody RegistroAdminDTO registro) {
        if (codigoRegistroAdmin == null || codigoRegistroAdmin.isBlank()
                || !MessageDigest.isEqual(codigoRegistroAdmin.getBytes(StandardCharsets.UTF_8),
                        registro.getCodigo_autorizacion().getBytes(StandardCharsets.UTF_8))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("code", "CODIGO_INVALIDO", "message", "El código de autorización institucional no es válido"));
        }
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombres(registro.getNombres());
        dto.setApellidos(registro.getApellidos());
        dto.setCorreo(registro.getCorreo());
        dto.setDni(registro.getCodigo_placa());
        dto.setTelefono(registro.getTelefono());
        dto.setDistrito(registro.getEntidad());
        dto.setContrasena(registro.getContrasena());
        return ResponseEntity.ok(usuarioService.registrarAdmin(dto));
    }

    // HU-01: solo un administrador crea otros administradores
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar un administrador", description = "Crea una cuenta con rol ADMIN. Requiere el código de autorización institucional.")
    @PostMapping("/registrar-admin")
    public ResponseEntity<UsuarioDTO> registrarAdmin(@Valid @RequestBody RegistroUsuarioDTO registro) {
        return ResponseEntity.ok(usuarioService.registrarAdmin(modelMapper.map(registro, UsuarioDTO.class)));
    }

    // HU-02: iniciar sesión (body: correo, contrasena y, opcionales, dni y rol)
    @Operation(summary = "Validar las credenciales de una cuenta", description = "Verifica correo y contraseña (opcionalmente dni y rol) y que la cuenta esté ACTIVA. Devuelve los datos del usuario sin la contraseña. Para obtener el token JWT usa POST /api/authenticate.")
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody UsuarioDTO datos) {
        return ResponseEntity.ok(usuarioService.iniciarSesion(
                datos.getCorreo(),
                datos.getDni(),
                datos.getContrasena(),
                datos.getRol()));
    }

    // HU-05: cerrar sesión
    @Operation(summary = "Cerrar sesión", description = "Confirma el cierre de sesión. El backend no guarda sesión (es sin estado): el cliente elimina su token.")
    @PostMapping("/logout")
    public String logout() {
        return "Sesión cerrada correctamente";
    }

    // HU-03 paso 1 (body: correo y dni)
    @Operation(summary = "Verificar la cuenta para recuperar la contraseña", description = "Paso 1: verifica que exista una cuenta con el correo y el DNI indicados.")
    @PostMapping("/recuperar-password")
    public String recuperarPassword(@RequestBody UsuarioDTO datos) {
        usuarioService.verificarCuenta(datos.getCorreo(), datos.getDni());
        return "Cuenta verificada. Ya puedes restablecer tu contraseña";
    }

    // HU-03 paso 2 (body: correo, dni y contrasena nueva)
    @Operation(summary = "Restablecer la contraseña", description = "Paso 2: recibe correo, DNI y la nueva contraseña (mínimo 8 caracteres) y la actualiza.")
    @PutMapping("/restablecer-password")
    public String restablecerPassword(@RequestBody UsuarioDTO datos) {
        usuarioService.restablecerContrasena(datos.getCorreo(), datos.getDni(), datos.getContrasena());
        return "Contraseña actualizada correctamente";
    }

    // HU-04: editar perfil
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id)")
    @Operation(summary = "Actualizar el perfil", description = "Actualiza nombres, apellidos, teléfono, distrito y dirección; los campos que no se envían no se modifican.")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.actualizar(id, usuarioDTO));
    }

    // HU-49: personalizar mensaje predeterminado de alerta SOS
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id)")
    @Operation(summary = "Personalizar el mensaje SOS", description = "Guarda el mensaje que reciben los contactos al activar el SOS. Es obligatorio y admite hasta 255 caracteres.")
    @PutMapping("/{id}/mensaje-sos")
    public ResponseEntity<UsuarioDTO> actualizarMensajeSos(@PathVariable Long id, @RequestParam String mensaje) {
        return ResponseEntity.ok(usuarioService.actualizarMensajeSos(id, mensaje));
    }

    // HU-47: accesos rápidos de la pantalla principal
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id)")
    @Operation(summary = "Datos de los accesos rápidos del panel", description = "Devuelve en una sola consulta los contactos, dispositivos, seguimiento activo, alertas activas y mensaje SOS del usuario.")
    @GetMapping("/{id}/accesos-rapidos")
    public ResponseEntity<Map<String, Object>> accesosRapidos(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.accesosRapidos(id));
    }

    // HU-48: suspender cuenta
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Suspender una cuenta (administrador)", description = "Cambia el estado a SUSPENDIDA y guarda el motivo, que es obligatorio.")
    @PutMapping("/{id}/suspender")
    public ResponseEntity<UsuarioDTO> suspender(@PathVariable Long id, @RequestParam String motivo) {
        return ResponseEntity.ok(usuarioService.suspender(id, motivo));
    }

    // HU-48: reactivar cuenta
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reactivar una cuenta (administrador)", description = "Vuelve a dejar la cuenta ACTIVA y borra el motivo de suspensión.")
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<UsuarioDTO> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.reactivar(id));
    }

    // HU-07: eliminar cuenta
    @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id)")
    @Operation(summary = "Eliminar una cuenta", description = "Elimina la cuenta del usuario y sus datos relacionados.")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }
}
