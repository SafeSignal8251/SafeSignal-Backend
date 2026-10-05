package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.Ruta;
import com.upc.safesignal.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    List<Ruta> findByUsuario(Usuario usuario);
}