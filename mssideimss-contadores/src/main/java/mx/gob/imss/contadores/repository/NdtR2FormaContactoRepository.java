package mx.gob.imss.contadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.imss.contadores.entity.NdtR2FormaContacto;

@Repository
public interface NdtR2FormaContactoRepository extends JpaRepository<NdtR2FormaContacto, Long> {
    
}
