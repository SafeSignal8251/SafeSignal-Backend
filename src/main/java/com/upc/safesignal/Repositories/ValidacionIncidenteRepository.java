package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.Incidente;
import com.upc.safesignal.Entities.Usuario;
import com.upc.safesignal.Entities.ValidacionIncidente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValidacionIncidenteRepository extends JpaRepository<ValidacionIncidente, Long> {

    List<ValidacionIncidente> findByIncidente(Incidente incidente);

    List<ValidacionIncidente> findByUsuario(Usuario usuario);

    List<ValidacionIncidente> findByIncidenteAndUsuario(Incidente incidente, Usuario usuario);

    @Query("SELECT COUNT(v) FROM ValidacionIncidente v WHERE v.incidente = :incidente AND v.tipo_voto = :tipo")
    long contarVotos(@Param("incidente") Incidente incidente, @Param("tipo") String tipo);
}