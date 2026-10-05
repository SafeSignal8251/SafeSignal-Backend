package com.upc.safesignal.ServiceImpl;

import com.upc.safesignal.Dtos.*;
import com.upc.safesignal.Entities.*;
import com.upc.safesignal.Repositories.*;
import com.upc.safesignal.Services.ContactoConfianzaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContactoConfianzaServiceImpl implements ContactoConfianzaService {

    @Autowired
    private ContactoConfianzaRepository contactoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ContactoConfianzaDTO> listar() {
        return aDTOs(contactoRepository.findAll());
    }

    @Override
    public ContactoConfianzaDTO obtenerPorId(Long id) {
        return aDTO(buscarContacto(id));
    }

    // HU-06: registrar contacto de confianza
    @Override
    public ContactoConfianzaDTO registrar(Long id_usuario, ContactoConfianzaDTO contactoDTO) {
        Usuario usuario = buscarUsuario(id_usuario);

        if (vacio(contactoDTO.getNombres()) || vacio(contactoDTO.getTelefono())) {
            throw new RuntimeException("El nombre y el teléfono del contacto son obligatorios");
        }
        for (ContactoConfianza c : contactoRepository.findByUsuario(usuario)) {
            if (contactoDTO.getTelefono().equals(c.getTelefono())) {
                throw new RuntimeException("Ya tienes un contacto con ese teléfono");
            }
        }

        ContactoConfianza contacto = modelMapper.map(contactoDTO, ContactoConfianza.class);
        contacto.setId_contacto(null);
        contacto.setUsuario(usuario);
        if (contacto.getPrioridad() == null) {
            contacto.setPrioridad(1);
        }
        if (contacto.getNotificar_sms() == null) {
            contacto.setNotificar_sms(true);
        }
        if (contacto.getNotificar_whatsapp() == null) {
            contacto.setNotificar_whatsapp(false);
        }
        return aDTO(contactoRepository.save(contacto));
    }

    @Override
    public List<ContactoConfianzaDTO> listarPorUsuario(Long id_usuario) {
        return aDTOs(contactoRepository.findByUsuarioOrderByPrioridadAsc(buscarUsuario(id_usuario)));
    }

    @Override
    public ContactoConfianzaDTO actualizar(Long id, ContactoConfianzaDTO datos) {
        ContactoConfianza contacto = buscarContacto(id);
        if (datos.getNombres() != null) contacto.setNombres(datos.getNombres());
        if (datos.getApellidos() != null) contacto.setApellidos(datos.getApellidos());
        if (datos.getTelefono() != null) contacto.setTelefono(datos.getTelefono());
        if (datos.getParentesco() != null) contacto.setParentesco(datos.getParentesco());
        if (datos.getPrioridad() != null) contacto.setPrioridad(datos.getPrioridad());
        if (datos.getNotificar_sms() != null) contacto.setNotificar_sms(datos.getNotificar_sms());
        if (datos.getNotificar_whatsapp() != null) contacto.setNotificar_whatsapp(datos.getNotificar_whatsapp());
        return aDTO(contactoRepository.save(contacto));
    }

    @Override
    public void eliminar(Long id) {
        contactoRepository.delete(buscarContacto(id));
    }

    // ---------- métodos privados ----------

    private ContactoConfianza buscarContacto(Long id) {
        return contactoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado con id: " + id));
    }

    private Usuario buscarUsuario(Long id_usuario) {
        return usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // entidad -> DTO (los ids de las relaciones se copian a mano)
    private ContactoConfianzaDTO aDTO(ContactoConfianza contacto) {
        ContactoConfianzaDTO dto = modelMapper.map(contacto, ContactoConfianzaDTO.class);
        if (contacto.getUsuario() != null) {
            dto.setId_usuario(contacto.getUsuario().getId_usuario());
        }
        return dto;
    }

    private List<ContactoConfianzaDTO> aDTOs(List<ContactoConfianza> contactos) {
        List<ContactoConfianzaDTO> resultado = new ArrayList<>();
        for (ContactoConfianza contacto : contactos) {
            resultado.add(aDTO(contacto));
        }
        return resultado;
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
