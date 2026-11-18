package com.upc.safesignal.service;

import com.upc.safesignal.Dtos.UsuarioDTO;
import com.upc.safesignal.Entities.Usuario;
import com.upc.safesignal.Repositories.UsuarioRepository;
import com.upc.safesignal.ServiceImpl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Pruebas unitarias de la capa de servicio (HU-01 a HU-04, HU-48) con Mockito
@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void preparar() {
        ReflectionTestUtils.setField(usuarioService, "passwordEncoder", encoder);
        ReflectionTestUtils.setField(usuarioService, "modelMapper", new ModelMapper());
    }

    private UsuarioDTO datosValidos() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombres("Ana");
        dto.setApellidos("Perez");
        dto.setCorreo("ana@correo.com");
        dto.setDni("12345678");
        dto.setContrasena("Clave12345");
        return dto;
    }

    private Usuario usuarioGuardado(String clavePlana, String estado) {
        Usuario u = new Usuario();
        u.setId_usuario(7L);
        u.setCorreo("ana@correo.com");
        u.setDni("12345678");
        u.setRol("CLIENTE");
        u.setEstado(estado);
        u.setContrasena(encoder.encode(clavePlana));
        return u;
    }

    // ---------- HU-01: registrar ----------

    @Test
    void registrar_cifraLaContrasenaYCreaCuentaCliente() {
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(List.of());
        when(usuarioRepository.findByDni("12345678")).thenReturn(List.of());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO resultado = usuarioService.registrar(datosValidos());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();
        assertNotEquals("Clave12345", guardado.getContrasena(), "la contraseña no se guarda en texto plano");
        assertTrue(encoder.matches("Clave12345", guardado.getContrasena()));
        assertEquals("CLIENTE", guardado.getRol());
        assertEquals("ACTIVA", guardado.getEstado());
        assertNull(resultado.getContrasena(), "la contraseña nunca se devuelve");
    }

    @Test
    void registrar_ignoraElRolAdminEnviadoPorElCliente() {
        UsuarioDTO dto = datosValidos();
        dto.setRol("ADMIN");
        when(usuarioRepository.findByCorreo(any())).thenReturn(List.of());
        when(usuarioRepository.findByDni(any())).thenReturn(List.of());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO resultado = usuarioService.registrar(dto);

        assertEquals("CLIENTE", resultado.getRol(), "nadie puede auto-asignarse el rol ADMIN");
    }

    @Test
    void registrar_conCorreoDuplicado_lanzaError() {
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(List.of(new Usuario()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.registrar(datosValidos()));

        assertTrue(ex.getMessage().contains("correo"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrar_conContrasenaCorta_lanzaError() {
        UsuarioDTO dto = datosValidos();
        dto.setContrasena("123");

        assertThrows(RuntimeException.class, () -> usuarioService.registrar(dto));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrar_sinCamposObligatorios_lanzaError() {
        UsuarioDTO dto = datosValidos();
        dto.setDni(" ");

        assertThrows(RuntimeException.class, () -> usuarioService.registrar(dto));
    }

    // ---------- HU-02: iniciar sesión ----------

    @Test
    void iniciarSesion_conCredencialesCorrectas_devuelveElUsuario() {
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(List.of(usuarioGuardado("Clave12345", "ACTIVA")));

        UsuarioDTO dto = usuarioService.iniciarSesion("ana@correo.com", "Clave12345");

        assertEquals(7L, dto.getId_usuario());
        assertNull(dto.getContrasena());
    }

    @Test
    void iniciarSesion_conContrasenaIncorrecta_lanzaError() {
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(List.of(usuarioGuardado("Clave12345", "ACTIVA")));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.iniciarSesion("ana@correo.com", "otraClave"));

        assertEquals("Correo o contraseña incorrectos", ex.getMessage());
    }

    @Test
    void iniciarSesion_conCorreoInexistente_noRevelaQueNoExiste() {
        when(usuarioRepository.findByCorreo("nadie@correo.com")).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.iniciarSesion("nadie@correo.com", "Clave12345"));

        assertEquals("Correo o contraseña incorrectos", ex.getMessage());
    }

    @Test
    void iniciarSesion_conCuentaSuspendida_lanzaError() {
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(List.of(usuarioGuardado("Clave12345", "SUSPENDIDA")));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.iniciarSesion("ana@correo.com", "Clave12345"));

        assertTrue(ex.getMessage().contains("suspendida"));
    }

    @Test
    void iniciarSesion_conRolQueNoCorresponde_lanzaError() {
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(List.of(usuarioGuardado("Clave12345", "ACTIVA")));

        assertThrows(RuntimeException.class,
                () -> usuarioService.iniciarSesion("ana@correo.com", null, "Clave12345", "ADMIN"));
    }

    // ---------- HU-03: restablecer contraseña ----------

    @Test
    void restablecerContrasena_guardaLaNuevaContrasenaCifrada() {
        Usuario u = usuarioGuardado("Clave12345", "ACTIVA");
        when(usuarioRepository.findByCorreoAndDni("ana@correo.com", "12345678")).thenReturn(List.of(u));

        usuarioService.restablecerContrasena("ana@correo.com", "12345678", "NuevaClave99");

        verify(usuarioRepository).save(u);
        assertTrue(encoder.matches("NuevaClave99", u.getContrasena()));
    }

    @Test
    void restablecerContrasena_conClaveCorta_lanzaError() {
        assertThrows(RuntimeException.class,
                () -> usuarioService.restablecerContrasena("ana@correo.com", "12345678", "corta"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void verificarCuenta_conDatosQueNoCoinciden_lanzaError() {
        when(usuarioRepository.findByCorreoAndDni("ana@correo.com", "00000000")).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> usuarioService.verificarCuenta("ana@correo.com", "00000000"));
    }

    // ---------- HU-04: editar perfil ----------

    @Test
    void actualizar_soloCambiaLosCamposEnviados_yNuncaElCorreoNiElRol() {
        Usuario u = usuarioGuardado("Clave12345", "ACTIVA");
        u.setNombres("Ana");
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(u));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO cambios = new UsuarioDTO();
        cambios.setTelefono("987654321");
        cambios.setCorreo("hacker@correo.com");
        cambios.setRol("ADMIN");

        UsuarioDTO resultado = usuarioService.actualizar(7L, cambios);

        assertEquals("987654321", resultado.getTelefono());
        assertEquals("Ana", resultado.getNombres());
        assertEquals("ana@correo.com", resultado.getCorreo());
        assertEquals("CLIENTE", resultado.getRol());
    }

    @Test
    void obtenerPorId_inexistente_lanzaError() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- HU-48: suspender / reactivar ----------

    @Test
    void suspender_sinMotivo_lanzaError() {
        assertThrows(RuntimeException.class, () -> usuarioService.suspender(7L, " "));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void suspender_cambiaElEstadoYGuardaElMotivo() {
        Usuario u = usuarioGuardado("Clave12345", "ACTIVA");
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(u));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO resultado = usuarioService.suspender(7L, "Uso indebido");

        assertEquals("SUSPENDIDA", resultado.getEstado());
        assertEquals("Uso indebido", resultado.getMotivo_suspension());
    }

    @Test
    void reactivar_limpiaElMotivo() {
        Usuario u = usuarioGuardado("Clave12345", "SUSPENDIDA");
        u.setMotivo_suspension("Uso indebido");
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(u));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO resultado = usuarioService.reactivar(7L);

        assertEquals("ACTIVA", resultado.getEstado());
        assertNull(resultado.getMotivo_suspension());
    }

    @Test
    void cambiarEstado_conValorInvalido_lanzaError() {
        assertThrows(RuntimeException.class, () -> usuarioService.cambiarEstado(7L, "BORRADA", null));
    }

    @Test
    void actualizarMensajeSos_demasiadoLargo_lanzaError() {
        assertThrows(RuntimeException.class, () -> usuarioService.actualizarMensajeSos(7L, "a".repeat(256)));
    }
}
