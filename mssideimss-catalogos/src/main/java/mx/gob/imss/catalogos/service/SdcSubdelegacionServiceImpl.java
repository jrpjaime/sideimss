package mx.gob.imss.catalogos.service;

import java.util.List;

import javax.sql.DataSource;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
 
import mx.gob.imss.catalogos.dto.SdcSubdelegacionDto;
import mx.gob.imss.catalogos.dto.SdcSubdelegacionFiltroDto;
 

@Service("sdcSubdelegacionService")
public class  SdcSubdelegacionServiceImpl implements SdcSubdelegacionService  { 

 	private final static Logger logger = LoggerFactory.getLogger(SdcSubdelegacionServiceImpl.class);

	@Autowired
	private DataSource datasource;

 
 
 

	
	/*
	* Metodo de consulta findAll
	*/
	@Override
	@Transactional
	public List<SdcSubdelegacionDto> findAllSdcSubdelegacion(SdcSubdelegacionFiltroDto sdcSubdelegacionFiltroDto){
		logger.info("findAllSdcSubdelegacion");
		List<SdcSubdelegacionDto> sdcSubdelegacionDtos=new ArrayList<>();
        Connection conn=null;
        Statement sm=null;
        ResultSet rs = null;
    	try {
    	 	 conn = datasource.getConnection();
    		 String query = "\r\n" + //
								"SELECT  ID_SUBDELEGACION, ID_DELEGACION, CVE_SUBDELEGACION, DES_SUBDELEGACION FROM SDC_SUBDELEGACION WHERE ID_DELEGACION="+ sdcSubdelegacionFiltroDto.getIdDelegacion() + " ORDER BY DES_SUBDELEGACION ";

			 logger.info("Sql: " + query);
    		 
    		 sm = conn.createStatement();
    		 rs =  sm.executeQuery(query);

         if (rs != null) {
             while (rs.next()) {
				Integer idSubdelegacion= Integer.parseInt(rs.getString(1));
				Integer idDelegacion= Integer.parseInt(rs.getString(2));
				String cveSubdelegacion= rs.getString( 3);
				String desSubdelegacion= rs.getString(4); 
		 
 
				SdcSubdelegacionDto sdcSubdelegacionDto = new SdcSubdelegacionDto();
				sdcSubdelegacionDto.setIdSubdelegacion(idSubdelegacion);
				sdcSubdelegacionDto.setIdDelegacion(idDelegacion);
				sdcSubdelegacionDto.setCveSubdelegacion(cveSubdelegacion);
				sdcSubdelegacionDto.setDesSubdelegacion(desSubdelegacion);
			 
				sdcSubdelegacionDtos.add(sdcSubdelegacionDto);
 
             }
         }



		 
    	}catch (SQLException e) { // Captura específicamente errores SQL
			logger.error("Error SQL en findAllPageableSwtAsegurado: {}", e.getMessage(), e);
			// Lanza una excepción personalizada o una RuntimeException
			throw new RuntimeException("Error al buscar subdelegaciones: ", e);
		} catch (Exception e) { // Captura cualquier otra excepción
			logger.error("Error inesperado en findAllPageableSwtAsegurado: {}", e.getMessage(), e);
			throw new RuntimeException("Ocurrió un error inesperado al buscar subdelegaciones: " , e);
		} finally {


    	    try {
    	        if (rs != null)
    	        	rs.close();

    	        if (sm != null)
    	        	sm.close();

    	        if (conn != null)
    	        	conn.close();

    	    } catch (SQLException sqlee) {
				logger.error("Error al cerrar recursos de base de datos: {}", sqlee.getMessage(), sqlee);
				// No relanzamos aquí, ya se ha reportado el error principal
       		}

        } 

		logger.info("sdcSubdelegacionDtos.size(): "+ sdcSubdelegacionDtos.size()); 
		return sdcSubdelegacionDtos;
	}
 
	}
