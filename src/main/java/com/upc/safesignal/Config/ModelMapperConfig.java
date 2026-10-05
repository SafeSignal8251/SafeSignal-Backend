package com.upc.safesignal.Config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.*;

@Configuration
public class ModelMapperConfig {

    // se inyecta con @Autowired en cualquier clase que lo necesite
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // los campos null del DTO no pisan los valores por defecto de la entidad
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        // solo mapea campos con el mismo nombre (los ids de las relaciones se asignan a mano)
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }
}
