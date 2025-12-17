package mx.gob.imss.contadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.imss.contadores.entity.NdtR1DatosPersonales;

@Repository
public interface NdtR1DatosPersonalesRepository extends JpaRepository<NdtR1DatosPersonales, Long> {
    
}
