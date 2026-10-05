package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.ContactoConfianza;
import com.upc.safesignal.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactoConfianzaRepository extends JpaRepository<ContactoConfianza, Long> {

    List<ContactoConfianza> findByUsuario(Usuario usuario);

    List<ContactoConfianza> findByUsuarioOrderByPrioridadAsc(Usuario usuario);
}