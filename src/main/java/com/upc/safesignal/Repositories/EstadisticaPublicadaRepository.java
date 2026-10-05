package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.EstadisticaPublicada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadisticaPublicadaRepository extends JpaRepository<EstadisticaPublicada, Long> {

    @Query("SELECT e FROM EstadisticaPublicada e ORDER BY e.fecha_publicacion DESC")
    List<EstadisticaPublicada> listarRecientes();
}