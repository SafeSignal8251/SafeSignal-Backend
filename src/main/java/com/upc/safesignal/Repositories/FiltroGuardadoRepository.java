package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.FiltroGuardado;
import com.upc.safesignal.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FiltroGuardadoRepository extends JpaRepository<FiltroGuardado, Long> {

    List<FiltroGuardado> findByUsuario(Usuario usuario);
}