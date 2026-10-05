package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.Incidente;
import com.upc.safesignal.Entities.Usuario;
import com.upc.safesignal.Entities.ZonaRiesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenteRepository extends JpaRepository<Incidente, Long> {

    List<Incidente> findByUsuario(Usuario usuario);

    List<Incidente> findByZona(ZonaRiesgo zona);

    List<Incidente> findByUsuarioAndEstado(Usuario usuario, String estado);

    List<Incidente> findByEstado(String estado);

    List<Incidente> findByDistrito(String distrito);

    // HU-20: incidentes dentro de un cuadro alrededor de un punto
    @Query("""
            SELECT i FROM Incidente i
            WHERE i.latitud BETWEEN :latMin AND :latMax
              AND i.longitud BETWEEN :lngMin AND :lngMax
            ORDER BY i.fecha_hora DESC
            """)
    List<Incidente> findEnCuadro(@Param("latMin") double latMin,
                                 @Param("latMax") double latMax,
                                 @Param("lngMin") double lngMin,
                                 @Param("lngMax") double lngMax);
}