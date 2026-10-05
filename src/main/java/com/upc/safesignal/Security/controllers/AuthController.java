package com.upc.safesignal.Security.controllers;

import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Security.dtos.*;
import com.upc.safesignal.Security.services.*;
import com.upc.safesignal.Security.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> autenticar(@RequestBody AuthRequestDTO authRequest) {
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

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", token);

        AuthResponseDTO respuesta = new AuthResponseDTO();
        respuesta.setJwt(token);
        respuesta.setId_usuario(usuario.getId_usuario());
        respuesta.setRoles(roles);
        return ResponseEntity.ok().headers(responseHeaders).body(respuesta);
    }
}
