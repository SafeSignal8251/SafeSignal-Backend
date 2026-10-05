package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LecturaSensorServiceImpl implements LecturaSensorService {

    @Autowired
    private LecturaSensorRepository lecturaRepository;

    @Autowired
    private DispositivoIoTRepository dispositivoRepository;

    @Autowired
    private AlertaService alertaService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<LecturaSensorDTO> listar() {
        return aDTOs(lecturaRepository.findAll());
    }

    @Override
    public LecturaSensorDTO obtenerPorId(Long id) {
        return aDTO(buscarLectura(id));
    }

    // Registra la lectura y, si es anómala, genera una alerta automática (HU-15)
    @Override
    public LecturaSensorDTO registrar(Long id_dispositivo, LecturaSensorDTO lecturaDTO) {
        DispositivoIoT dispositivo = buscarDispositivo(id_dispositivo);
        if (lecturaDTO.getValor() == null) {
            throw new RuntimeException("El valor de la lectura es obligatorio");
        }

        LecturaSensor lectura = modelMapper.map(lecturaDTO, LecturaSensor.class);
        lectura.setId_lectura(null);
        lectura.setDispositivo(dispositivo);
        lectura.setFecha_hora(LocalDateTime.now());
        if (lectura.getAnomalia() == null) {
            lectura.setAnomalia(false);
        }
        LecturaSensor guardada = lecturaRepository.save(lectura);

        dispositivo.setFecha_ultima(LocalDateTime.now());
        dispositivoRepository.save(dispositivo);

        if (guardada.getAnomalia()) {
            log.warn("Anomalía detectada en el dispositivo id: {}", id_dispositivo);
            AlertaDTO alerta = new AlertaDTO();
            alerta.setTipo_alerta("ANOMALIA");
            alertaService.registrarConDispositivo(
                    dispositivo.getUsuario().getId_usuario(), dispositivo.getId_dispositivo(), alerta);
        }
        return aDTO(guardada);
    }

    @Override
    public List<LecturaSensorDTO> listarPorDispositivo(Long id_dispositivo) {
        return aDTOs(lecturaRepository.findByDispositivo(buscarDispositivo(id_dispositivo)));
    }

    @Override
    public List<LecturaSensorDTO> listarAnomalias() {
        return aDTOs(lecturaRepository.findByAnomalia(true));
    }

    @Override
    public List<LecturaSensorDTO> listarAnomaliasPorDispositivo(Long id_dispositivo) {
        return aDTOs(lecturaRepository.findByDispositivoAndAnomalia(buscarDispositivo(id_dispositivo), true));
    }

    @Override
    public void eliminar(Long id) {
        lecturaRepository.delete(buscarLectura(id));
    }

    // ---------- métodos privados ----------

    private LecturaSensor buscarLectura(Long id) {
        return lecturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lectura no encontrada con id: " + id));
    }

    private DispositivoIoT buscarDispositivo(Long id_dispositivo) {
        return dispositivoRepository.findById(id_dispositivo)
                .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private LecturaSensorDTO aDTO(LecturaSensor lectura) {
        LecturaSensorDTO dto = modelMapper.map(lectura, LecturaSensorDTO.class);
        if (lectura.getDispositivo() != null) {
            dto.setId_dispositivo(lectura.getDispositivo().getId_dispositivo());
        }
        return dto;
    }

    private List<LecturaSensorDTO> aDTOs(List<LecturaSensor> lecturas) {
        List<LecturaSensorDTO> resultado = new ArrayList<>();
        for (LecturaSensor lectura : lecturas) {
            resultado.add(aDTO(lectura));
        }
        return resultado;
    }
}
