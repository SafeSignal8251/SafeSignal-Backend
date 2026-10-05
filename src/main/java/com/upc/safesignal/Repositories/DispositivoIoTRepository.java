package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.DispositivoIoT;
import com.upc.safesignal.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispositivoIoTRepository extends JpaRepository<DispositivoIoT, Long> {

    List<DispositivoIoT> findByUsuario(Usuario usuario);

    List<DispositivoIoT> findByUsuarioAndEstado(Usuario usuario, String estado);

    List<DispositivoIoT> findByEstado(String estado);

    @Query("SELECT d FROM DispositivoIoT d WHERE d.codigo_dispositivo = :codigo")
    List<DispositivoIoT> findByCodigo(@Param("codigo") String codigo);
}