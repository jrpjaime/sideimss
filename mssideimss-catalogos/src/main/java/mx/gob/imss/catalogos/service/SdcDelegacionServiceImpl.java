package mx.gob.imss.catalogos.service;

import java.util.List;
 
import java.sql.Connection;
import java.sql.ResultSet; 
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import jakarta.transaction.Transactional;
import mx.gob.imss.catalogos.dto.SdcDelegacionDto; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import java.util.ArrayList; 

 

@Service("sdcDelegacionService")
public class  SdcDelegacionServiceImpl implements SdcDelegacionService  { 
	private final static Logger logger = LoggerFactory.getLogger(SdcDelegacionServiceImpl.class);

	@Autowired
	private DataSource datasource;

 

	/*
	* Metodo de consulta findAll
	*/
	@Override
	@Transactional
	public List<SdcDelegacionDto> findAllSdcDelegacion(){
		logger.info("findAllSdcDelegacion");
		List<SdcDelegacionDto> sdcDelegacionDtos=new ArrayList<>();
        Connection conn=null;
        Statement sm=null;
        ResultSet rs = null;
    	try {
    	 	 conn = datasource.getConnection();
    		 String query = "SELECT ID_DELEGACION, CVE_DELEGACION, DES_DELEGACION FROM SDC_DELEGACION ORDER BY DES_DELEGACION ";

			 logger.info("Sql: " + query);
    		 
    		 sm = conn.createStatement();
    		 rs =  sm.executeQuery(query);

         if (rs != null) {
             while (rs.next()) {
				Integer idDelegacion= Integer.parseInt(rs.getString(1));
				String cveDelegacion= rs.getString( 2);
				String desDelegacion= rs.getString(3);
 
				SdcDelegacionDto sdcDelegacionDto = new SdcDelegacionDto();
				sdcDelegacionDto.setIdDelegacion(idDelegacion);
				sdcDelegacionDto.setCveDelegacion(cveDelegacion);
				sdcDelegacionDto.setDesDelegacion(desDelegacion);
			 
				sdcDelegacionDtos.add(sdcDelegacionDto);
 
             }
         }



		 
    	}catch (SQLException e) { // Captura específicamente errores SQL
			logger.error("Error SQL en findAllPageableSwtAsegurado: {}", e.getMessage(), e);
			// Lanza una excepción personalizada o una RuntimeException
			throw new RuntimeException("Error al buscar delegaciones: ", e);
		} catch (Exception e) { // Captura cualquier otra excepción
			logger.error("Error inesperado en findAllPageableSwtAsegurado: {}", e.getMessage(), e);
			throw new RuntimeException("Ocurrió un error inesperado al buscar delegaciones: " , e);
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


		logger.info("sdcDelegacionDtos.size(): "+ sdcDelegacionDtos.size());

		
 
		return sdcDelegacionDtos;
	}



 
	}
