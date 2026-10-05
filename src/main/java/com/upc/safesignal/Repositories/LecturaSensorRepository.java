package com.upc.safesignal.Repositories;

import com.upc.safesignal.Entities.DispositivoIoT;
import com.upc.safesignal.Entities.LecturaSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LecturaSensorRepository extends JpaRepository<LecturaSensor, Long> {

    List<LecturaSensor> findByDispositivo(DispositivoIoT dispositivo);

    List<LecturaSensor> findByDispositivoAndAnomalia(
            DispositivoIoT dispositivo,
            Boolean anomalia
    );

    List<LecturaSensor> findByAnomalia(Boolean anomalia);
}