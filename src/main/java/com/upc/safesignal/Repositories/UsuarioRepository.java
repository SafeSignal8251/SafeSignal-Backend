package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByCorreo(String correo);

    List<Usuario> findByCorreoAndDni(String correo, String dni);

    List<Usuario> findByDni(String dni);

    List<Usuario> findByEstado(String estado);

    List<Usuario> findByRol(String rol);
}