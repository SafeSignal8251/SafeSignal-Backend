package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.Seguimiento;
import com.upc.safesignal.Entities.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {

    List<Ubicacion> findBySeguimiento(Seguimiento seguimiento);

    // HU-10: ubicaciones del seguimiento, la más reciente primero
    @Query("SELECT u FROM Ubicacion u WHERE u.seguimiento = :seguimiento ORDER BY u.fecha_hora DESC")
    List<Ubicacion> ultimaPorSeguimiento(@Param("seguimiento") Seguimiento seguimiento);
}
