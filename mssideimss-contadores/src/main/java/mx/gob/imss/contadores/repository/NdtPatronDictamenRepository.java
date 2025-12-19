package mx.gob.imss.contadores.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.gob.imss.contadores.entity.NdtPatronDictamen;

 
@Repository
public interface NdtPatronDictamenRepository extends JpaRepository<NdtPatronDictamen, Long> {
    @Query(value = "SELECT COUNT(1) " +
           "FROM MGPBDTU9X.NDT_PATRON_DICTAMEN PD " +  
           "INNER JOIN MGPBDTU9X.NDC_EJERCICIO_FISCAL EF ON EF.CVE_ID_EJER_FISCAL = PD.CVE_ID_EJER_FISCAL " +
           "INNER JOIN MGPBDTU9X.NDT_PATRON_DICTAMEN_CPA CP ON CP.CVE_ID_PATRON_DICTAMEN = PD.CVE_ID_PATRON_DICTAMEN " +
           "INNER JOIN MGPBDTU9X.NDT_CONTADOR_PUBLICO_AUT CPA ON CP.CVE_ID_CPA = CPA.CVE_ID_CPA " +
           "INNER JOIN MGPBDTU9X.NDC_ESTADO_DICTAMEN E ON E.CVE_ID_ESTADO_DICTAMEN = PD.CVE_ID_ESTADO_DICTAMEN " +
           "AND E.DES_ESTADO_DICTAMEN = 'EN PROCESO' "+
           "WHERE CPA.NUM_REGISTRO_CPA = :numRegistroCpa", nativeQuery = true)
    int countDictamenesPorRegistroCpa(@Param("numRegistroCpa") Integer numRegistroCpa);
}