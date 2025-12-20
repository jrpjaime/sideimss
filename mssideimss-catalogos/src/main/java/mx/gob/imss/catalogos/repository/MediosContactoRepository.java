package mx.gob.imss.catalogos.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.gob.imss.catalogos.dto.MedioContactoProjection;
import mx.gob.imss.catalogos.entity.NdtFormaContacto;

import java.util.List;

@Repository
public interface MediosContactoRepository extends JpaRepository<NdtFormaContacto, Long> {

    /*
    Ejecuta la Consulta A: Obtiene el tipo de contacto y descripción de la persona física (marcando el origen como 'CONTADOR').
Ejecuta la Consulta B: Obtiene el tipo de contacto y descripción del contador en el sistema SIDEIMSS (marcando el origen como 'TODOS').
Combina y Elimina Duplicados: Aquí está el secreto. El UNION (a diferencia del UNION ALL) compara las filas de ambos resultados y elimina las que sean exactamente iguales. Si un correo aparece en ambos sistemas con el mismo tipo, solo te devolverá uno (a menos que el campo rfcAsociado los haga diferentes). */
    @Query(value = """
        ---OBTENER CORREO 1 DE IMSS DIGITAL ---
        SELECT FC.CVE_ID_TIPO_CONTACTO AS "tipoContacto", 
               FC.DES_FORMA_CONTACTO AS "desFormaContacto", 
               'CONTADOR' AS "rfcAsociado"
        FROM MGPBDTU9X.DIT_PERSONA_FISICA P 
        INNER JOIN MGPBDTU9X.DIT_PERSONAF_CONTACTO PFC ON P.CVE_ID_PERSONA = PFC.CVE_ID_PERSONA
        INNER JOIN MGPBDTU9X.DIT_FORMA_CONTACTO FC ON PFC.CVE_ID_FORMA_CONTACTO = FC.CVE_ID_FORMA_CONTACTO
        INNER JOIN MGPBDTU9X.DIT_LLAVE_PERSONA DP ON DP.CVE_ID_PERSONA = PFC.CVE_ID_PERSONA
        WHERE DP.RFC = :rfc
        
        UNION
        
        ---OBTENER LOS CORREOS 2 Y 3 EN SIDEIMSS
        SELECT FC.CVE_ID_TIPO_CONTACTO AS "tipoContacto", 
               FC.DES_FORMA_CONTACTO AS "desFormaContacto", 
               'TODOS' AS "rfcAsociado"
        FROM MGPBDTU9X.NDT_CONTADOR_PUBLICO_AUT CPA
        INNER JOIN MGPBDTU9X.DIT_LLAVE_PERSONA DP ON CPA.CVE_ID_PERSONA = DP.CVE_ID_PERSONA
        INNER JOIN MGPBDTU9X.NDT_R1_DATOS_PERSONALES R ON CPA.CVE_ID_CPA = R.CVE_ID_CPA
        INNER JOIN MGPBDTU9X.NDT_R1_FORMACONTACTO F ON F.CVE_ID_R1_DATOS_PERSONALES = R.CVE_ID_R1_DATOS_PERSONALES
        INNER JOIN MGPBDTU9X.NDT_FORMA_CONTACTO FC ON FC.CVE_ID_FORMA_CONTACTO = F.CVE_ID_FORMA_CONTACTO
        WHERE DP.RFC = :rfc
        AND R.FEC_REGISTRO_BAJA IS NULL
        """, nativeQuery = true)
    List<MedioContactoProjection> findMediosContactoByRfc(@Param("rfc") String rfc);
}