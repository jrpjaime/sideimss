package mx.gob.imss.autodeterminaciones.service;
 
import java.util.List; 
import java.sql.Connection;
import java.sql.ResultSet; 
import java.sql.SQLException;
import java.sql.Statement; 
import java.util.ArrayList; 
 
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;
import mx.gob.imss.autodeterminaciones.dto.SwtAseguradoDto;
import mx.gob.imss.autodeterminaciones.dto.SwtMovimientoDto; 

@Service("swtAseguradoService")
public class SwtAseguradoServiceImpl implements SwtAseguradoService {


	private final static Logger logger = LoggerFactory.getLogger(SwtAseguradoServiceImpl.class);

 

	@Autowired
	private DataSource datasource;


    @Override
	@Transactional
	public Page<SwtAseguradoDto> findAllPageableSwtAsegurado(SwtAseguradoDto swtAseguradoDto, Pageable pageable){

        
		logger.info("...... INICIA findAllPageableSwtAsegurado"); 
		List<SwtAseguradoDto> swtAseguradoDtos = new ArrayList<SwtAseguradoDto>();
        Connection conn=null;
        Statement sm=null;
        ResultSet rs = null;
    	try {

			logger.info("swtAseguradoDto.getRefRfc(): "+ swtAseguradoDto.getRefRfc()); 
            logger.info("swtAseguradoDto.getCveRegistroPatronal(): "+ swtAseguradoDto.getCveRegistroPatronal());  
			
			logger.info("swtAseguradoDto.getNumNss(): "+ swtAseguradoDto.getNumNss()); 
			logger.info("swtAseguradoDto.getNomNombre(): "+ swtAseguradoDto.getNomNombre()); 
   

			String filtros="WHERE  SASEG.IND_ACTIVO = 1";


			if(swtAseguradoDto.getCveRegistroPatronal() == null || swtAseguradoDto.getCveRegistroPatronal().trim().isEmpty()){
				logger.error("Error: debe proporcionar el Registro Patronal para la búsqueda.");
				throw new IllegalArgumentException("El campo 'Registro Patronal' es requerido.");
			}
		
/*
			if(swtAseguradoDto.getRefRfc()!=null  && !swtAseguradoDto.getRefRfc().trim().equals("")){
				filtros=  getfiltroWhere(filtros);
				filtros= filtros + "SSD.REF_RFC  LIKE '%"+swtAseguradoDto.getRefRfc()+"%' \r\n "; 
			}
*/
	 
/*
			if(swtAseguradoDto.getCveRegistroPatronal()!=null  && !swtAseguradoDto.getCveRegistroPatronal().trim().equals("")){
				filtros=  getfiltroWhere(filtros);
				filtros= filtros + "SASEG.CVE_NRP  LIKE '%"+swtAseguradoDto.getCveRegistroPatronal()+"%' \r\n"; 
			}


			if(swtAseguradoDto.getNumNss()!=null  && !swtAseguradoDto.getNumNss().trim().equals("")){
				filtros=  getfiltroWhere(filtros);
				filtros= filtros + "SASEG.REF_NSS  LIKE '%"+swtAseguradoDto.getNumNss()+"%' \r\n "; 
			}
 
*/
    	 	 conn = datasource.getConnection();
    		 String query =  " SELECT SASEG.ID_EMI_ASEGURADO,  SASEG.CVE_NRP, SASEG.REF_NSS, SASEG.REF_CURP, SASEG.NOM_ASEGURADO    \r\n" + //
                                 "FROM SUAWT_EMI_ASEGURADO SASEG \r\n" + //
                                  filtros;

		  
			 logger.info("findAllPageableSwtAsegurado Sql: \r\n" + query);   		 
    		 sm = conn.createStatement();
    		 rs =  sm.executeQuery(query);


         if (rs != null) {
             while (rs.next()) {

				

				Long idAsegurado= Long.parseLong(rs.getString(1));
				String cveRegistroPatronal=  rs.getString(2);
				String numNss=  rs.getString(3);
				String refCurp=  rs.getString(4);
				String nomNombre=  rs.getString(5);
 

				SwtAseguradoDto swtAseguradoDtoAux = new SwtAseguradoDto();
				swtAseguradoDtoAux.setIdAsegurado(idAsegurado);
				swtAseguradoDtoAux.setCveRegistroPatronal(cveRegistroPatronal);
                swtAseguradoDtoAux.setNumNss(numNss);
                swtAseguradoDtoAux.setRefCurp(refCurp);
                swtAseguradoDtoAux.setNomNombre(nomNombre);

 
				swtAseguradoDtos.add(swtAseguradoDtoAux);
				  

				 
             }
         }



		 
    	}catch (SQLException e) { // Captura específicamente errores SQL
			logger.error("Error SQL en findAllPageableSwtAsegurado: {}", e.getMessage(), e);
			// Lanza una excepción personalizada o una RuntimeException
			throw new RuntimeException("Error al buscar asegurados: ", e);
		} catch (Exception e) { // Captura cualquier otra excepción
			logger.error("Error inesperado en findAllPageableSwtAsegurado: {}", e.getMessage(), e);
			throw new RuntimeException("Ocurrió un error inesperado al buscar asegurados: " , e);
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


		logger.info("swtAseguradoDtos.size(): "+ swtAseguradoDtos.size());
 
 

		Page<SwtAseguradoDto> resultado = null;
		int start = (int) pageable.getOffset();
		int end = (start + pageable.getPageSize()) > swtAseguradoDtos.size() ? swtAseguradoDtos.size() : (start + pageable.getPageSize());
		if (start > end) {
		 start = end;
		}
		 resultado = new PageImpl<>(swtAseguradoDtos.subList(start, end), pageable, swtAseguradoDtos.size());

		 logger.info("...... TERMINA findAllPageableSwtAsegurado "); 
		return resultado;
	}


    private String getfiltroWhere(String filtros){
		logger.info("1filtros: "+ filtros);
		logger.info(" filtros.length(): "+ filtros.length());
		if(filtros.trim().equals("")){
			filtros=  " WHERE ";
		} else

		if(!filtros.trim().equals("")){
			filtros= filtros + " AND " ;
		}
		logger.info("2filtros: "+ filtros);
		return filtros;
	}




    @Override
	@Transactional
	public Page<SwtMovimientoDto> findAllPageableSwtMovimientos(SwtMovimientoDto swtMovimientoDto, Pageable pageable){

		if(swtMovimientoDto.getCveRegistroPatronal() == null || swtMovimientoDto.getCveRegistroPatronal().trim().isEmpty()){
			logger.error("Error: debe proporcionar el Registro Patronal para la búsqueda.");
			throw new IllegalArgumentException("El campo 'Registro Patronal' es requerido.");
		}

	
		logger.info("...... INICIA findAllPageableSwtAsegurado"); 
		List<SwtMovimientoDto> swtMovimientoDtos = new ArrayList<SwtMovimientoDto>();
        Connection conn=null;
        Statement sm=null;
        ResultSet rs = null;
    	try {

			logger.info("swtMovimientoDto.getRefRfc(): "+ swtMovimientoDto.getRefRfc()); 
            logger.info("swtMovimientoDto.getCveRegistroPatronal(): "+ swtMovimientoDto.getCveRegistroPatronal());  
			
			logger.info("swtMovimientoDto.getNumNss(): "+ swtMovimientoDto.getNumNss());  
   

			String filtros="";
 
/*
			if(swtMovimientoDto.getRefRfc()!=null  && !swtMovimientoDto.getRefRfc().trim().equals("")){
				filtros=  getfiltroWhere(filtros);
				filtros= filtros + "SSD.REF_RFC  LIKE '%"+swtMovimientoDto.getRefRfc()+"%' \r\n "; 
			}
*/
	 
/*
			if(swtMovimientoDto.getCveRegistroPatronal()!=null  && !swtMovimientoDto.getCveRegistroPatronal().trim().equals("")){
				filtros=  getfiltroWhere(filtros);
				filtros= filtros + "SASEG.CVE_NRP  LIKE '%"+swtMovimientoDto.getCveRegistroPatronal()+"%' \r\n"; 
			}


			if(swtMovimientoDto.getNumNss()!=null  && !swtMovimientoDto.getNumNss().trim().equals("")){
				filtros=  getfiltroWhere(filtros);
				filtros= filtros + "SASEG.REF_NSS  LIKE '%"+swtMovimientoDto.getNumNss()+"%' \r\n "; 
			}
 
*/
    	 	 conn = datasource.getConnection();
    		 String query =  "SELECT ID_EMI_MOV_ASEGURADO, CVE_NRP, REF_NSS, ID_TIPO_MOV, NUM_SALARIO_BASE_COT, TO_CHAR( FEC_INICIO_MOV , 'YYYY/MM/DD' ) AS FEC_INICIO_MOV FROM SUAWT_EMI_MOV_ASEGURADO \r\n" + //
                                  filtros;

		  
			 logger.info("findAllPageableSwtAsegurado Sql: \r\n" + query);   		 
    		 sm = conn.createStatement();
    		 rs =  sm.executeQuery(query);


         if (rs != null) {
             while (rs.next()) {

				
 				String idMovimiento= rs.getString(1);
			 
				String cveRegistroPatronal=  rs.getString(2);
				String numNss=  rs.getString(3);
				Integer idTipoMovimiento= Integer.parseInt(rs.getString(4));
				String salSalarioDiarioIntegrado=  rs.getString(5);
				String fecInicio =   rs.getString(6);
				

				SwtMovimientoDto swtMovimientoDtoAux = new SwtMovimientoDto();
				swtMovimientoDtoAux.setIdMovimiento(idMovimiento);
				swtMovimientoDtoAux.setCveRegistroPatronal(cveRegistroPatronal);
                swtMovimientoDtoAux.setNumNss(numNss);
                swtMovimientoDtoAux.setIdTipoMovimiento(idTipoMovimiento);
                swtMovimientoDtoAux.setSalSalarioDiarioIntegrado(salSalarioDiarioIntegrado); 
				swtMovimientoDtoAux.setFecInicio(fecInicio);
 
				swtMovimientoDtos.add(swtMovimientoDtoAux);
				  

				 
             }
         }



		 
		 
    	}catch (SQLException e) { // Captura específicamente errores SQL
			logger.error("Error SQL en findAllPageableSwtAsegurado: {}", e.getMessage(), e);
			// Lanza una excepción personalizada o una RuntimeException
			throw new RuntimeException("Error al buscar asegurados: ", e);
		} catch (Exception e) { // Captura cualquier otra excepción
			logger.error("Error inesperado en findAllPageableSwtAsegurado: {}", e.getMessage(), e);
			throw new RuntimeException("Ocurrió un error inesperado al buscar asegurados: " , e);
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


		logger.info("swtMovimientoDtos.size(): "+ swtMovimientoDtos.size());
 
 

		Page<SwtMovimientoDto> resultado = null;
		int start = (int) pageable.getOffset();
		int end = (start + pageable.getPageSize()) > swtMovimientoDtos.size() ? swtMovimientoDtos.size() : (start + pageable.getPageSize());
		if (start > end) {
		 start = end;
		}
		 resultado = new PageImpl<>(swtMovimientoDtos.subList(start, end), pageable, swtMovimientoDtos.size());

		 logger.info("...... TERMINA findAllPageableSwtAsegurado "); 
		return resultado;
	}

 




}
