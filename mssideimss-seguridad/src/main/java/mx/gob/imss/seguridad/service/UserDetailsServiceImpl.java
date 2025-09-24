package mx.gob.imss.seguridad.service;

import mx.gob.imss.seguridad.entity.SdtUsuario;
 
import mx.gob.imss.seguridad.repository.SdtUsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SdtUsuarioRepository sdtUsuarioRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Call Database to validate
      
          SdtUsuario sdtUsuario=sdtUsuarioRepository.findSdtUsuarioByDesUsuario(username).iterator().next(); 
        if(sdtUsuario == null) {
            throw  new UsernameNotFoundException(username);
        }
        return new User(sdtUsuario.getDesUsuario(), sdtUsuario.getDesPassword(), new ArrayList<>());
    }
}

