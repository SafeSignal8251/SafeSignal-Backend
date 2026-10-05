package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;

public interface ContactoConfianzaService {

    List<ContactoConfianzaDTO> listar();

    ContactoConfianzaDTO obtenerPorId(Long id);

    // HU-06: registrar contacto de confianza
    ContactoConfianzaDTO registrar(Long id_usuario, ContactoConfianzaDTO contactoDTO);

    List<ContactoConfianzaDTO> listarPorUsuario(Long id_usuario);

    ContactoConfianzaDTO actualizar(Long id, ContactoConfianzaDTO datos);

    void eliminar(Long id);
}
