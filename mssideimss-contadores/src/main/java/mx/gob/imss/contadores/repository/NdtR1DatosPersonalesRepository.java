package mx.gob.imss.contadores.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.gob.imss.contadores.entity.NdtR1DatosPersonales;

@Repository
public interface NdtR1DatosPersonalesRepository extends JpaRepository<NdtR1DatosPersonales, Long> {
        // Busca el registro activo (FEC_REGISTRO_BAJA IS NULL) para ese contador
    // Basado en tu SQL: AND RDP.FEC_REGISTRO_BAJA IS NULL
    @Query("SELECT r FROM NdtR1DatosPersonales r WHERE r.cveIdCpa = :cveIdCpa AND r.fecRegistroBaja IS NULL")
    Optional<NdtR1DatosPersonales> findRegistroActivoByCpa(@Param("cveIdCpa") Long cveIdCpa);
}
