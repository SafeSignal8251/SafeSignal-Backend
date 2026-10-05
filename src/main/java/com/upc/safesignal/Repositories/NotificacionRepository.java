package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.Alerta;
import com.upc.safesignal.Entities.ContactoConfianza;
import com.upc.safesignal.Entities.Notificacion;
import com.upc.safesignal.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByAlerta(Alerta alerta);

    List<Notificacion> findByContacto(ContactoConfianza contacto);

    List<Notificacion> findByEstado(String estado);

    @Query("SELECT n FROM Notificacion n WHERE n.usuario = :usuario ORDER BY n.fecha_envio DESC")
    List<Notificacion> findByUsuarioReciente(@Param("usuario") Usuario usuario);
}