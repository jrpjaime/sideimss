package mx.gob.imss.contadores.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.gob.imss.contadores.entity.NdtR3Colegio;

@Repository
public interface NdtR3ColegioRepository extends JpaRepository<NdtR3Colegio, Long> {
    @Query("SELECT r FROM NdtR3Colegio r WHERE r.cveIdCpa = :cveIdCpa AND r.fecRegistroBaja IS NULL")
    Optional<NdtR3Colegio> findRegistroActivoByCpa(@Param("cveIdCpa") Long cveIdCpa);
}
