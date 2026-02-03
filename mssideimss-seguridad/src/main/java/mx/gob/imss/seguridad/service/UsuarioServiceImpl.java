package mx.gob.imss.seguridad.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.gob.imss.seguridad.dto.DITPersonaProjection;
import mx.gob.imss.seguridad.dto.UsuarioDto;
import mx.gob.imss.seguridad.repository.DITPersonaRepository;

@Service("usuarioService")
public class UsuarioServiceImpl implements UsuarioService  {
 
    @Autowired
    private DITPersonaRepository ditPersonaRepository;

    @Override
    public Optional<UsuarioDto> getUsuarioInfoByRfc(String rfc) {
        Optional<DITPersonaProjection> personaProjection = ditPersonaRepository.findPersonaAndCpaByRfc(rfc);

        if (personaProjection.isPresent()) {
            DITPersonaProjection dITPersonaProjection = personaProjection.get();
            UsuarioDto usuarioDto = new UsuarioDto();
            usuarioDto.setRfc(dITPersonaProjection.getRfc());
            usuarioDto.setNombre(dITPersonaProjection.getNomNombre());
            usuarioDto.setPrimerApellido(dITPersonaProjection.getNomPrimerApellido());
            usuarioDto.setSegundoApellido(dITPersonaProjection.getNomSegundoApellido());
            usuarioDto.setCurp(dITPersonaProjection.getCurp()); 
            if (dITPersonaProjection.getNumRegistroCpa() != null && !dITPersonaProjection.getNumRegistroCpa().isEmpty()) {
                usuarioDto.setNumeroRegistroImss(dITPersonaProjection.getNumRegistroCpa()); 
            } else {
                usuarioDto.setNumeroRegistroImss(null);  
            }
          
            usuarioDto.setCveIdCpa(dITPersonaProjection.getCveIdCpa()); 

            usuarioDto.setCveIdEstadoCpa(dITPersonaProjection.getCveIdEstadoCpa());  
            usuarioDto.setIndBaja(dITPersonaProjection.getFecRegistroBaja() != null || (dITPersonaProjection.getCveIdEstadoCpa() != null && dITPersonaProjection.getCveIdEstadoCpa() == 10));

            return Optional.of(usuarioDto);
        }
        return Optional.empty();
    }
}