package com.upc.safesignal.Security.services;

import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.*;

/*
 Busca al usuario por correo, convierte su rol en GrantedAuthority ("ROLE_ADMIN" / "ROLE_CLIENTE")
 y devuelve el UserDetails que Spring Security usa para validar la contraseña y los permisos.
 Lo usa el AuthController (login) y el JwtRequestFilter (cada request).
*/
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        List<Usuario> encontrados = usuarioRepository.findByCorreo(correo);
        if (encontrados.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        Usuario usuario = encontrados.get(0);

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getCorreo())
                .password(usuario.getContrasena())
                .authorities(authorities)
                .disabled(!"ACTIVA".equalsIgnoreCase(usuario.getEstado())) // cuenta suspendida no entra
                .build();
    }
}