package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Entities.ZonaRiesgo;
import com.upc.safesignal.Repositories.ZonaRiesgoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/*
 Resuelve la zona de riesgo de un punto del mapa: usa la más cercana y, si todavía no existe
 ninguna, crea una a partir de los datos del reporte o de la suscripción. Así un cliente puede
 reportar o suscribirse aunque el administrador no haya cargado zonas.
*/
@Slf4j
@Component
public class ZonaResolver {

    @Autowired
    private ZonaRiesgoRepository zonaRepository;

    public ZonaRiesgo masCercanaOCrear(Double latitud, Double longitud, String nombre, String nivelRiesgo) {
        ZonaRiesgo mejor = null;
        double menor = Double.MAX_VALUE;
        for (ZonaRiesgo z : zonaRepository.findAll()) {
            if (z.getLatitud() == null || z.getLongitud() == null) {
                continue;
            }
            double d = distanciaMetros(latitud, longitud, z.getLatitud(), z.getLongitud());
            if (d < menor) {
                menor = d;
                mejor = z;
            }
        }
        if (mejor != null) {
            return mejor;
        }
        ZonaRiesgo nueva = new ZonaRiesgo();
        nueva.setNombre(nombre != null && !nombre.isBlank() ? recortar(nombre, 100) : "Zona reportada");
        nueva.setNivel_riesgo(nivelRiesgo != null ? nivelRiesgo : "MEDIO");
        nueva.setPuntaje_riesgo(0.0);
        nueva.setLatitud(latitud);
        nueva.setLongitud(longitud);
        nueva.setRadio_metros(500.0);
        nueva.setRadio_geocerca(500.0);
        nueva.setFecha_actualizacion(LocalDateTime.now());
        nueva.setEstado(true);
        log.info("Zona de riesgo creada automáticamente: {}", nueva.getNombre());
        return zonaRepository.save(nueva);
    }

    private static String recortar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(0, max);
    }

    private static double distanciaMetros(double lat1, double lng1, double lat2, double lng2) {
        double radioTierra = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return radioTierra * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
