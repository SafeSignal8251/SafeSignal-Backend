package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.SuscripcionZona;
import com.upc.safesignal.Entities.Usuario;
import com.upc.safesignal.Entities.ZonaRiesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuscripcionZonaRepository extends JpaRepository<SuscripcionZona, Long> {

    List<SuscripcionZona> findByUsuario(Usuario usuario);

    List<SuscripcionZona> findByZona(ZonaRiesgo zona);

    List<SuscripcionZona> findByUsuarioAndZona(Usuario usuario, ZonaRiesgo zona);

}