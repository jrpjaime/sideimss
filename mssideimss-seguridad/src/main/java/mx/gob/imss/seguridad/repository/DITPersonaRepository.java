package mx.gob.imss.seguridad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.gob.imss.seguridad.dto.DITPersonaProjection;
import mx.gob.imss.seguridad.entity.DITPersona;

import java.util.Optional;

@Repository
public interface DITPersonaRepository extends JpaRepository<DITPersona, Long> {
/*
    @Query(value = "SELECT DP.CVE_ID_PERSONA as cveIdPersona, DP.NOM_NOMBRE as nomNombre, DP.NOM_PRIMER_APELLIDO as nomPrimerApellido, DP.NOM_SEGUNDO_APELLIDO as nomSegundoApellido, DP.CURP, DP.RFC, " +
                   "NCPA.CVE_ID_CPA as cveIdCpa, NCPA.NUM_REGISTRO_CPA as numRegistroCpa, " +
                   " DP.FEC_REGISTRO_BAJA as fecRegistroBaja " +
                   "FROM MGPBDTU9X.DIT_PERSONA  DP " +
                   "JOIN MGPBDTU9X.NDT_CONTADOR_PUBLICO_AUT NCPA ON NCPA.CVE_ID_PERSONA = DP.CVE_ID_PERSONA " + 
                   //" AND NCPA.FEC_REGISTRO_BAJA IS NULL " +
                   " WHERE DP.RFC = :rfc "+ 
                   //" AND DP.FEC_REGISTRO_BAJA IS NULL " +
                   " ",
           nativeQuery = true)
    Optional<DITPersonaProjection> findPersonaAndCpaByRfc(@Param("rfc") String rfc);*/

        @Query(value = "SELECT DP.CVE_ID_PERSONA as cveIdPersona, DP.NOM_NOMBRE as nomNombre, " +
                   "DP.NOM_PRIMER_APELLIDO as nomPrimerApellido, DP.NOM_SEGUNDO_APELLIDO as nomSegundoApellido, " +
                   "DP.CURP, DP.RFC, NCPA.CVE_ID_CPA as cveIdCpa, NCPA.NUM_REGISTRO_CPA as numRegistroCpa, " +
                   "NCPA.CVE_ID_ESTADO_CPA as cveIdEstadoCpa, " +  
                   "DP.FEC_REGISTRO_BAJA as fecRegistroBaja " +
                   "FROM MGPBDTU9X.DIT_PERSONA DP " +
                   "JOIN MGPBDTU9X.NDT_CONTADOR_PUBLICO_AUT NCPA ON NCPA.CVE_ID_PERSONA = DP.CVE_ID_PERSONA " + 
                   "WHERE DP.RFC = :rfc", nativeQuery = true)
    Optional<DITPersonaProjection> findPersonaAndCpaByRfc(@Param("rfc") String rfc);

}