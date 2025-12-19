package mx.gob.imss.catalogos.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext; 
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import mx.gob.imss.catalogos.dto.DespachoRequestDto;
import mx.gob.imss.catalogos.dto.DespachoResponseDto;

import java.util.List;

@Service("despachoService")
public class DespachoServiceImpl implements DespachoService {
    private final static Logger logger = LoggerFactory.getLogger(DespachoServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DespachoResponseDto consultarDatosDespacho(DespachoRequestDto request) {
        logger.info("Consultando datos reales de despacho para RFC: {}", request.getRfc());

        // SQL basado en la estructura R2 (Despacho) de SIDEIMSS
        String sql = "SELECT " +
                     "  D.RFC_DESPACHO, " +
                     "  D.NOM_NOMBRE_DESPACHO, " +
                     "  R2.CVE_ID_TIPO_SOCIEDAD, " +
                     "  TS.DES_TIPO_SOCIEDAD, " +
                     "  R2.CVE_ID_CARGO_CONTADOR, " +
                     "  CC.DES_CARGO_CONTADOR, " +
                     "  R2.NUM_TELEFONO_FIJO, " +
                     "  R2.IND_TIENE_TRABAJADORES, " + // Asumiendo 1/0 o S/N
                     "  R2.NUM_TRABAJADORES " +
                     "FROM MGPBDTU9X.NDT_CONTADOR_PUBLICO_AUT DDP " +
                     "INNER JOIN MGPBDTU9X.DIT_PERSONA DPER ON DPER.CVE_ID_PERSONA = DDP.CVE_ID_PERSONA " +
                     "INNER JOIN MGPBDTU9X.NDT_R2_DESPACHO R2 ON R2.CVE_ID_CPA = DDP.CVE_ID_CPA " +
                     "LEFT JOIN MGPBDTU9X.NDT_DESPACHO D ON D.CVE_ID_DESPACHO = R2.CVE_ID_DESPACHO " +
                     "LEFT JOIN MGPBDTU9X.NDC_TIPO_SOCIEDAD TS ON TS.CVE_ID_TIPO_SOCIEDAD = R2.CVE_ID_TIPO_SOCIEDAD " +
                     "LEFT JOIN MGPBDTU9X.NDC_CARGO_CONTADOR CC ON CC.CVE_ID_CARGO_CONTADOR = R2.CVE_ID_CARGO_CONTADOR " +
                     "WHERE DPER.RFC = :rfc " +
                     "AND R2.FEC_REGISTRO_BAJA IS NULL " +
                     "ORDER BY R2.FEC_REGISTRO DESC";

        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("rfc", request.getRfc());

            List<Object[]> results = query.getResultList();

            if (results.isEmpty()) {
                logger.warn("No se encontró información de despacho para el RFC: {}", request.getRfc());
                return null;
            }

            // Mapeo del primer resultado encontrado
            Object[] row = results.get(0);
            DespachoResponseDto response = new DespachoResponseDto();
            
            response.setRfcDespacho(row[0] != null ? row[0].toString() : "");
            response.setNombreRazonSocial(row[1] != null ? row[1].toString() : "");
            response.setCveIdTipoSociedad(row[2] != null ? row[2].toString() : "");
            response.setDesTipoSociedad(row[3] != null ? row[3].toString() : "");
            response.setCveIdCargoContador(row[4] != null ? row[4].toString() : "");
            response.setDesCargoContador(row[5] != null ? row[5].toString() : "");
            response.setTelefonoFijo(row[6] != null ? row[6].toString() : "");
            
            // Lógica para transformar indicador de trabajadores (ej: de 1/0 a Si/No)
            String indTrabajadores = row[7] != null ? row[7].toString() : "0";
            response.setTieneTrabajadores(
                (indTrabajadores.equals("1") || indTrabajadores.equalsIgnoreCase("S") || indTrabajadores.equalsIgnoreCase("Si")) 
                ? "Si" : "No"
            );
            
            response.setNumeroTrabajadores(row[8] != null ? row[8].toString() : "0");

            return response;

        } catch (Exception e) {
            logger.error("Error al consultar la base de datos para despacho: {}", e.getMessage());
            return null;
        }
    }


    
}
