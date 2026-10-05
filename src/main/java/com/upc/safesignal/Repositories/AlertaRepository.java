package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.Alerta;
import com.upc.safesignal.Entities.DispositivoIoT;
import com.upc.safesignal.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByUsuario(Usuario usuario);

    List<Alerta> findByDispositivo(DispositivoIoT dispositivo);

    List<Alerta> findByUsuarioAndEstado(Usuario usuario, String estado);

    List<Alerta> findByEstado(String estado);

    @Query("SELECT a FROM Alerta a WHERE a.usuario = :usuario ORDER BY a.fecha_hora DESC")
    List<Alerta> historialPorUsuario(@Param("usuario") Usuario usuario);
}