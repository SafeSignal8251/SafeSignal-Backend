package com.upc.safesignal.Security.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Security.dtos.*;
import com.upc.safesignal.Security.services.*;
import com.upc.safesignal.Security.util.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Tag(name = "Autenticación", description = "Inicio de sesión con JWT. Obtén el token aquí y pégalo en el botón Authorize.")
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // HU-02: iniciar sesión (devuelve el token JWT)
    @Operation(summary = "Iniciar sesión y obtener el token JWT", description = "Recibe correo y contraseña y devuelve el token JWT. El token se envía en el encabezado Authorization (Bearer) de las demás peticiones. Endpoint público.")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> autenticar(@Valid @RequestBody AuthRequestDTO authRequest) {
        // lanza BadCredentialsException si el correo o la contraseña no coinciden
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getCorreo(), authRequest.getContrasena()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getCorreo());
        Usuario usuario = usuarioRepository.findByCorreo(authRequest.getCorreo()).get(0);
        String token = jwtUtil.generateToken(userDetails, usuario.getId_usuario());

        Set<String> roles = new HashSet<>();
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            roles.add(authority.getAuthority());
        }

        log.info("Autenticación correcta del usuario id: {}", usuario.getId_usuario());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", token);

        AuthResponseDTO respuesta = new AuthResponseDTO();
        respuesta.setJwt(token);
        respuesta.setId_usuario(usuario.getId_usuario());
        respuesta.setRoles(roles);
        return ResponseEntity.ok().headers(responseHeaders).body(respuesta);
    }
}
