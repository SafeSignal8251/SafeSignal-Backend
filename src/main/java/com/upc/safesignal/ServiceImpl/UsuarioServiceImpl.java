package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContactoConfianzaService contactoService;

    @Autowired
    private DispositivoIoTService dispositivoService;

    @Autowired
    private SeguimientoService seguimientoService;

    @Autowired
    private AlertaService alertaService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<UsuarioDTO> listar() {
        return aDTOs(usuarioRepository.findAll());
    }

    @Override
    public UsuarioDTO obtenerPorId(Long id) {
        return aDTO(buscarUsuario(id));
    }

    @Override
    public boolean existeCorreo(String correo) {
        return !usuarioRepository.findByCorreo(correo).isEmpty();
    }

    @Override
    public boolean existeDni(String dni) {
        return !usuarioRepository.findByDni(dni).isEmpty();
    }

    // HU-01: crear cuenta
    @Override
    public UsuarioDTO registrar(UsuarioDTO usuarioDTO) {
        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
        return aDTO(crearUsuario(usuario));
    }

    // HU-01: un administrador crea otra cuenta de administrador
    @Override
    public UsuarioDTO registrarAdmin(UsuarioDTO usuarioDTO) {
        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
        Usuario creado = crearUsuario(usuario);
        creado.setRol("ADMIN");
        return aDTO(usuarioRepository.save(creado));
    }

    // HU-02: iniciar sesión
    @Override
    public UsuarioDTO iniciarSesion(String correo, String contrasena) {
        return iniciarSesion(correo, null, contrasena, null);
    }

    // dni y rol son opcionales
    @Override
    public UsuarioDTO iniciarSesion(String correo, String dni, String contrasena, String rol) {
        List<Usuario> encontrados = usuarioRepository.findByCorreo(correo);
        if (encontrados.isEmpty()) {
            log.warn("Inicio de sesión fallido: correo no registrado");
            throw new RuntimeException("Correo o contraseña incorrectos");
        }
        Usuario usuario = encontrados.get(0);

        if (contrasena == null || !passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            log.warn("Inicio de sesión fallido: contraseña incorrecta");
            throw new RuntimeException("Correo o contraseña incorrectos");
        }
        if (!vacio(dni) && !dni.equals(usuario.getDni())) {
            throw new RuntimeException("Correo o contraseña incorrectos");
        }
        if (!vacio(rol) && !rol.equalsIgnoreCase(usuario.getRol())) {
            throw new RuntimeException("El tipo de cuenta no corresponde");
        }
        if (!"ACTIVA".equalsIgnoreCase(usuario.getEstado())) {
            throw new RuntimeException("La cuenta se encuentra suspendida");
        }

        log.info("Inicio de sesión correcto del usuario id: {}", usuario.getId_usuario());
        return aDTO(usuario);
    }

    // HU-03 paso 1: verificar que la cuenta existe
    @Override
    public void verificarCuenta(String correo, String dni) {
        buscarPorCorreoYDni(correo, dni);
    }

    // HU-03 paso 2: restablecer contraseña
    @Override
    public void restablecerContrasena(String correo, String dni, String nuevaContrasena) {
        if (nuevaContrasena == null || nuevaContrasena.length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
        Usuario usuario = buscarPorCorreoYDni(correo, dni);
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
        log.info("Contraseña restablecida del usuario id: {}", usuario.getId_usuario());
    }

    // HU-04: editar perfil
    @Override
    public UsuarioDTO actualizar(Long id, UsuarioDTO datos) {
        Usuario usuario = buscarUsuario(id);
        if (datos.getNombres() != null) usuario.setNombres(datos.getNombres());
        if (datos.getApellidos() != null) usuario.setApellidos(datos.getApellidos());
        if (datos.getTelefono() != null) usuario.setTelefono(datos.getTelefono());
        if (datos.getDistrito() != null) usuario.setDistrito(datos.getDistrito());
        if (datos.getDireccion() != null) usuario.setDireccion(datos.getDireccion());
        return aDTO(usuarioRepository.save(usuario));
    }

    // HU-49: personalizar el mensaje predeterminado de la alerta SOS
    @Override
    public UsuarioDTO actualizarMensajeSos(Long id, String mensaje) {
        if (vacio(mensaje)) {
            throw new RuntimeException("El mensaje no puede estar vacío");
        }
        if (mensaje.length() > 255) {
            throw new RuntimeException("El mensaje no puede superar los 255 caracteres");
        }
        Usuario usuario = buscarUsuario(id);
        usuario.setMensaje_sos(mensaje);
        return aDTO(usuarioRepository.save(usuario));
    }

    // HU-47: datos para los accesos rápidos de la pantalla principal
    @Override
    public Map<String, Object> accesosRapidos(Long id) {
        Usuario usuario = buscarUsuario(id);
        Map<String, Object> accesos = new HashMap<>();
        accesos.put("mensaje_sos", usuario.getMensaje_sos());
        accesos.put("contactos", contactoService.listarPorUsuario(id));
        accesos.put("dispositivos", dispositivoService.listarPorUsuario(id));
        accesos.put("seguimiento_activo", seguimientoService.listarActivosPorUsuario(id));
        accesos.put("alertas_activas", alertaService.listarPorUsuarioYEstado(id, "ACTIVA"));
        return accesos;
    }

    // HU-48: suspender cuenta
    @Override
    public UsuarioDTO suspender(Long id, String motivo) {
        if (vacio(motivo)) {
            throw new RuntimeException("El motivo de suspensión es obligatorio");
        }
        Usuario usuario = buscarUsuario(id);
        usuario.setEstado("SUSPENDIDA");
        usuario.setMotivo_suspension(motivo);
        log.warn("Cuenta suspendida, usuario id: {}", id);
        return aDTO(usuarioRepository.save(usuario));
    }

    // HU-48: reactivar cuenta
    @Override
    public UsuarioDTO reactivar(Long id) {
        Usuario usuario = buscarUsuario(id);
        usuario.setEstado("ACTIVA");
        usuario.setMotivo_suspension(null);
        log.info("Cuenta reactivada, usuario id: {}", id);
        return aDTO(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioDTO cambiarEstado(Long id, String estado, String motivo) {
        if ("SUSPENDIDA".equalsIgnoreCase(estado)) {
            return suspender(id, motivo);
        }
        if ("ACTIVA".equalsIgnoreCase(estado)) {
            return reactivar(id);
        }
        throw new RuntimeException("Estado no válido: use ACTIVA o SUSPENDIDA");
    }

    @Override
    public List<UsuarioDTO> listarPorEstado(String estado) {
        return aDTOs(usuarioRepository.findByEstado(estado.toUpperCase()));
    }

    // HU-07: eliminar cuenta
    @Override
    public void eliminar(Long id) {
        log.warn("Eliminando usuario con id: {}", id);
        usuarioRepository.delete(buscarUsuario(id));
    }

    // ---------- métodos privados ----------

    // Valida los datos, cifra la contraseña y guarda la cuenta como CLIENTE
    private Usuario crearUsuario(Usuario usuario) {
        if (vacio(usuario.getNombres()) || vacio(usuario.getApellidos())
                || vacio(usuario.getCorreo()) || vacio(usuario.getDni())) {
            throw new RuntimeException("Nombres, apellidos, correo y DNI son obligatorios");
        }
        if (usuario.getContrasena() == null || usuario.getContrasena().length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
        if (!usuarioRepository.findByCorreo(usuario.getCorreo()).isEmpty()) {
            throw new RuntimeException("Ya existe una cuenta registrada con ese correo");
        }
        if (!usuarioRepository.findByDni(usuario.getDni()).isEmpty()) {
            throw new RuntimeException("Ya existe una cuenta registrada con ese DNI");
        }

        usuario.setId_usuario(null);
        usuario.setRol("CLIENTE");
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuario.setFecha_registro(LocalDateTime.now());
        usuario.setEstado("ACTIVA");
        usuario.setMotivo_suspension(null);

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado con id: {}", guardado.getId_usuario());
        return guardado;
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    private Usuario buscarPorCorreoYDni(String correo, String dni) {
        List<Usuario> encontrados = usuarioRepository.findByCorreoAndDni(correo, dni);
        if (encontrados.isEmpty()) {
            throw new RuntimeException("No existe una cuenta con esos datos");
        }
        return encontrados.get(0);
    }

    // entidad -> DTO (la contraseña nunca se devuelve)
    private UsuarioDTO aDTO(Usuario usuario) {
        UsuarioDTO dto = modelMapper.map(usuario, UsuarioDTO.class);
        dto.setContrasena(null);
        return dto;
    }

    private List<UsuarioDTO> aDTOs(List<Usuario> usuarios) {
        List<UsuarioDTO> resultado = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            resultado.add(aDTO(usuario));
        }
        return resultado;
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
