package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.Comentario;
import com.upc.safesignal.Entities.Incidente;
import com.upc.safesignal.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByUsuario(Usuario usuario);

    List<Comentario> findByIncidente(Incidente incidente);
}