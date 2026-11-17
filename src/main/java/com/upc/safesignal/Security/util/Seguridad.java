package com.upc.safesignal.Security.util;

import com.upc.safesignal.Entities.Usuario;
import com.upc.safesignal.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 Autorización a nivel de objeto: evita que un usuario autenticado actúe en nombre de otro
 cambiando el id_usuario de la URL. Se usa dentro de @PreAuthorize:
   @PreAuthorize("hasRole('ADMIN') or @seguridad.esUsuario(#id_usuario)")
*/
@Component("seguridad")
public class Seguridad {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // true si el id recibido pertenece al usuario dueño del token JWT de la petición
    public boolean esUsuario(Long id) {
        if (id == null) {
            return false;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            return false;
        }
        List<Usuario> encontrados = usuarioRepository.findByCorreo(auth.getName());
        return !encontrados.isEmpty() && id.equals(encontrados.get(0).getId_usuario());
    }
}
