package mx.gob.imss.contadores.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext; 
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import mx.gob.imss.contadores.dto.DespachoRequestDto;
import mx.gob.imss.contadores.dto.DespachoResponseDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
 

import java.util.List;

@Service("despachoService")
public class DespachoServiceImpl implements DespachoService {
    private final static Logger logger = LoggerFactory.getLogger(DespachoServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DespachoResponseDto consultarDatosDespacho(DespachoRequestDto request) {
        logger.info("Consultando datos reales de despacho para RFC: {}", request.getRfc()); 
        
        // 1. SQL con alias explícitos en MAYÚSCULAS
String sql = "SELECT " +
             "  PM.RFC as RFC_D, PM.DENOMINACION_RAZON_SOCIAL as NOM_D, " +
             "  R2.IND_TIPO_CPA as TIPO_S, " +
             "  (CASE R2.IND_TIPO_CPA WHEN 1 THEN 'DESPACHO' WHEN 2 THEN 'INDEPENDIENTE' ELSE 'OTRO' END) as DES_S, " +
             "  R2.CARGO_QUE_DESEMPENA as CARGO_ID, R2.CARGO_QUE_DESEMPENA as CARGO_DES, " +
             "  FC.DES_FORMA_CONTACTO as TEL, " + // Cambio aquí
             "  R2.IND_CUENTACON_TRAB as TIENE_TRAB, " +
             "  R2.NUM_TRABAJADORES_CONTRATADOS as NUM_TRAB " +
             "FROM MGPBDTU9X.NDT_CONTADOR_PUBLICO_AUT DDP " +
             "INNER JOIN MGPBDTU9X.DIT_PERSONA DPER ON DPER.CVE_ID_PERSONA = DDP.CVE_ID_PERSONA " +
             "INNER JOIN MGPBDTU9X.NDT_R2_DESPACHO R2 ON R2.CVE_ID_CPA = DDP.CVE_ID_CPA " +
             "LEFT JOIN MGPBDTU9X.NDT_DESPACHOS D ON D.CVE_ID_DESPACHO = R2.CVE_ID_DESPACHO " +
             "LEFT JOIN MGPBDTU9X.DIT_PERSONA_MORAL PM ON D.CVE_ID_PERSONA_MORAL = PM.CVE_ID_PERSONA_MORAL " +
             "LEFT JOIN MGPBDTU9X.NDT_R2_FORMACONTACTO RFC2 ON RFC2.CVE_ID_R2_DESPACHO = R2.CVE_ID_R2_DESPACHO AND RFC2.FEC_REGISTRO_BAJA IS NULL " +
             "LEFT JOIN MGPBDTU9X.NDT_FORMA_CONTACTO FC ON FC.CVE_ID_FORMA_CONTACTO = RFC2.CVE_ID_FORMA_CONTACTO AND FC.CVE_ID_TIPO_CONTACTO = 2 " +
             "WHERE DPER.RFC = :rfc " +
             "AND R2.FEC_REGISTRO_BAJA IS NULL " +
             "ORDER BY R2.FEC_REGISTRO_ALTA DESC";

        try {
            // 2. Usamos Tuple.class para un mapeo seguro por nombre
            Query query = entityManager.createNativeQuery(sql, Tuple.class);
            query.setParameter("rfc", request.getRfc());

            List<Tuple> results = query.getResultList();

            if (results.isEmpty()) {
                return null;
            }

            Tuple row = results.get(0);
            DespachoResponseDto response = new DespachoResponseDto();
            
            response.setRfcDespacho(getString(row, "RFC_D"));
            response.setNombreRazonSocial(getString(row, "NOM_D"));
            response.setCveIdTipoSociedad(getString(row, "TIPO_S"));
            response.setDesTipoSociedad(getString(row, "DES_S"));
            response.setCveIdCargoContador(getString(row, "CARGO_ID"));
            response.setDesCargoContador(getString(row, "CARGO_DES"));
            response.setTelefonoFijo(getString(row, "TEL"));
            
            // 3. Lógica del indicador: En el Save guardas "1" para Sí. Aquí lo recuperamos.
            String indTrab = getString(row, "TIENE_TRAB");
            if ("1".equals(indTrab) || "S".equalsIgnoreCase(indTrab) || "Si".equalsIgnoreCase(indTrab)) {
                response.setTieneTrabajadores("Si");
            } else {
                response.setTieneTrabajadores("No");
            }
            
            response.setNumeroTrabajadores(getString(row, "NUM_TRAB").isEmpty() ? "0" : getString(row, "NUM_TRAB"));

            return response;

        } catch (Exception e) {
            logger.error("Error al consultar el despacho: {}", e.getMessage());
            return null;
        }
    }

    // Método auxiliar para evitar nulos y limpiar espacios (Oracle CHAR types)
    private String getString(Tuple row, String alias) {
        Object val = row.get(alias);
        return (val != null) ? val.toString().trim() : "";
    }

    
}
