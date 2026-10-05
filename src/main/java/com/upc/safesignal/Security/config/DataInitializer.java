package com.upc.safesignal.Security.config;

import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByRol("ADMIN").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombres("Administrador");
            admin.setApellidos("SafeSignal");
            admin.setCorreo("admin@safesignal.com");
            admin.setDni("00000000");
            admin.setContrasena(passwordEncoder.encode("Admin12345"));
            admin.setRol("ADMIN");
            admin.setEstado("ACTIVA");
            admin.setFecha_registro(LocalDateTime.now());
            usuarioRepository.save(admin);
        }
    }
}
