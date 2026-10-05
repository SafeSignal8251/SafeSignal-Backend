package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.ZonaRiesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZonaRiesgoRepository extends JpaRepository<ZonaRiesgo, Long> {

    List<ZonaRiesgo> findByEstado(Boolean estado);

    List<ZonaRiesgo> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT z FROM ZonaRiesgo z WHERE z.nivel_riesgo = :nivel")
    List<ZonaRiesgo> findByNivel(@Param("nivel") String nivel);
}