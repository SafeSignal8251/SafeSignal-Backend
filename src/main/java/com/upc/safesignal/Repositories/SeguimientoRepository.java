package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.Seguimiento;
import com.upc.safesignal.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {

    List<Seguimiento> findByUsuario(Usuario usuario);

    List<Seguimiento> findByUsuarioAndEstado(Usuario usuario, String estado);

    List<Seguimiento> findByEstado(String estado);
}