package com.upc.safesignal.Services;

import com.upc.safesignal.Dtos.*;

import java.util.List;

public interface UbicacionService {

    List<UbicacionDTO> listar();

    UbicacionDTO obtenerPorId(Long id);

    // HU-10: registrar un punto de la ubicación en tiempo real
    UbicacionDTO registrar(Long id_seguimiento, UbicacionDTO ubicacionDTO);

    List<UbicacionDTO> listarPorSeguimiento(Long id_seguimiento);

    // HU-10: última ubicación compartida del seguimiento
    UbicacionDTO ultima(Long id_seguimiento);

    void eliminar(Long id);
}
