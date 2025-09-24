package mx.gob.imss.catalogos.service;

import java.util.List;

import java.sql.Connection;
import java.sql.ResultSet; 
import java.sql.SQLException;
import java.sql.Statement;


import javax.sql.DataSource;
 

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

 
import mx.gob.imss.catalogos.dto.DitPatronGeneralDto;
import mx.gob.imss.catalogos.dto.DitPatronGeneralFiltroDto;
import mx.gob.imss.catalogos.entity.DitPatronGeneral;
import mx.gob.imss.catalogos.repository.DitPatronGeneralRepository;

@Service("ditPatronGeneralService")
public class  DitPatronGeneralServiceImpl implements DitPatronGeneralService  { 
	private final static Logger logger = LoggerFactory.getLogger(DitPatronGeneralServiceImpl.class);

	@Autowired
	private DataSource datasource;

	@Autowired
	private DitPatronGeneralRepository ditPatronGeneralRepository;

	/*
	 * Metodo de consulta por idPatronGeneral
	*/
	@Override
	@Transactional
	public DitPatronGeneralDto findDitPatronGeneralByIdPatronGeneral(Integer idPatronGeneral){
		ModelMapper modelMapper = new ModelMapper(); 
		DitPatronGeneral ditPatronGeneral=ditPatronGeneralRepository.findDitPatronGeneralByIdPatronGeneral(idPatronGeneral); 
		DitPatronGeneralDto ditPatronGeneralDto = modelMapper.map(ditPatronGeneral, DitPatronGeneralDto.class); 
		return ditPatronGeneralDto;
	}

	/*
	 * Metodo de consulta si existe el registro por idPatronGeneral
	*/
	@Override
	@Transactional
	public boolean existsByIdPatronGeneral(Integer idPatronGeneral){
		return ditPatronGeneralRepository.existsByIdPatronGeneral(idPatronGeneral);
	}

	/*
	* Metodo de consulta findAll
	*/
	@Override
	@Transactional
	public List<DitPatronGeneralDto> findAllDitPatronGeneral(){
		ModelMapper modelMapper = new ModelMapper(); 
		List<DitPatronGeneral> ditPatronGenerals=ditPatronGeneralRepository.findAll(); 
		List<DitPatronGeneralDto> ditPatronGeneralDtos = new ArrayList<DitPatronGeneralDto>();

		 for (DitPatronGeneral ditPatronGeneral : ditPatronGenerals) { 
			DitPatronGeneralDto ditPatronGeneralDto = modelMapper.map(ditPatronGeneral, DitPatronGeneralDto.class); 
			ditPatronGeneralDtos.add(ditPatronGeneralDto); 
		 } 

		return ditPatronGeneralDtos;
	}

	/*
	* Metodo de consulta findAll
	*/
	@Override
	@Transactional
	public Page<DitPatronGeneralDto> findAllDitPatronGeneral(Pageable pageable){
		List<DitPatronGeneral> ditPatronGenerals=ditPatronGeneralRepository.findAll(); 
		ModelMapper modelMapper = new ModelMapper(); 
		List<DitPatronGeneralDto> ditPatronGeneralDtos = new ArrayList<DitPatronGeneralDto>();

		 for (DitPatronGeneral ditPatronGeneral : ditPatronGenerals) { 
			DitPatronGeneralDto ditPatronGeneralDto = modelMapper.map(ditPatronGeneral, DitPatronGeneralDto.class); 
			ditPatronGeneralDtos.add(ditPatronGeneralDto); 
		 } 

		Page<DitPatronGeneralDto> resultado = null;
		int start = (int) pageable.getOffset();
		int end = (start + pageable.getPageSize()) > ditPatronGeneralDtos.size() ? ditPatronGeneralDtos.size() : (start + pageable.getPageSize());
		if (start > end) {
		 start = end;
		}
		 resultado = new PageImpl<>(ditPatronGeneralDtos.subList(start, end), pageable, ditPatronGeneralDtos.size());
		return resultado;
	}

	
		/*
	* Metodo de consulta findAll
	*/
	@Override
	@Transactional
	public Page<DitPatronGeneralDto> findAllPageableDitPatronGeneral(DitPatronGeneralFiltroDto ditPatronGeneralFiltroDto, Pageable pageable){

		logger.info("................findAllPageableDitPatronGeneral "); 

		List<DitPatronGeneral> ditPatronGenerals=new ArrayList<>();
        Connection conn=null;
        Statement sm=null;
        ResultSet rs = null;
    	try {


			 //Nota ajustar la consulta para que se realice en la tabla correcta
			 logger.info("ditPatronGeneralFiltroDto.getRfc(): "+ ditPatronGeneralFiltroDto.getRfc()); 
			 logger.info("ditPatronGeneralFiltroDto.getRegistroPatronal(): "+ ditPatronGeneralFiltroDto.getRegistroPatronal());
    	 	 conn = datasource.getConnection();
    		 String query = "SELECT ID_PATRON_GENERAL, DENOMINACION_RAZON_SOCIAL, RFC, REGISTRO_PATRONAL, "
				          + " CVE_DELEGACION, DES_DELEGACION, CVE_SUBDELEGACION, DES_SUBDELEGACION "
						  + " FROM DIT_PATRON_GENERAL WHERE  RFC='" + ditPatronGeneralFiltroDto.getRfc().trim() + "'";

						  
			if(ditPatronGeneralFiltroDto !=null)	{
				if(ditPatronGeneralFiltroDto.getRegistroPatronal()!=null)	{
					if(!ditPatronGeneralFiltroDto.getRegistroPatronal().trim().equals(""))	{
						query = query + " AND REGISTRO_PATRONAL LIKE '%" + ditPatronGeneralFiltroDto.getRegistroPatronal().trim().toUpperCase() + "%'"; 
					}
				}

				if(ditPatronGeneralFiltroDto.getDesDelegacion()!=null)	{
					if(!ditPatronGeneralFiltroDto.getDesDelegacion().trim().equals(""))	{
						query = query + " AND DES_DELEGACION LIKE '%" + ditPatronGeneralFiltroDto.getDesDelegacion().trim().toUpperCase() + "%'"; 
					}
				}

				if(ditPatronGeneralFiltroDto.getDesSubdelegacion()!=null)	{
					if(!ditPatronGeneralFiltroDto.getDesSubdelegacion().trim().equals(""))	{
						query = query + " AND DES_SUBDELEGACION LIKE '%" + ditPatronGeneralFiltroDto.getDesSubdelegacion().trim().toUpperCase() + "%'"; 
					}
				}
			}
 		

    		 System.out.println("Sql: " + query);
    		 sm = conn.createStatement();
    		 rs =  sm.executeQuery(query);


         if (rs != null) {
             while (rs.next()) {

				

				Long idPatronGeneral= Long.parseLong(rs.getString(1));;
				String denominacionRazonSocial= rs.getString(2);
				String rfc= rs.getString(3);
				String registroPatronal= rs.getString(4);
				String cveDelegacion= rs.getString(5);
				String desDelegacion= rs.getString(6);
				String cveSubdelegacion= rs.getString(7);
				String desSubdelegacion= rs.getString(8);

				DitPatronGeneral ditPatronGeneral = new DitPatronGeneral();
				 ditPatronGeneral.setIdPatronGeneral(idPatronGeneral);
				 ditPatronGeneral.setDenominacionRazonSocial(denominacionRazonSocial);
				 ditPatronGeneral.setRfc(rfc); 
				 ditPatronGeneral.setRegistroPatronal(registroPatronal); 
				 ditPatronGeneral.setCveDelegacion(cveDelegacion); 
				 ditPatronGeneral.setDesDelegacion(desDelegacion); 
				 ditPatronGeneral.setCveSubdelegacion(cveSubdelegacion); 
				 ditPatronGeneral.setDesSubdelegacion(desSubdelegacion); 
			 
                 ditPatronGenerals.add(ditPatronGeneral);

				 
             }
         }




    	}catch (SQLException e) { // Captura específicamente errores SQL
			logger.error("Error SQL en findAllPageableSwtAsegurado: {}", e.getMessage(), e);
			// Lanza una excepción personalizada o una RuntimeException
			throw new RuntimeException("Error al buscar Registros Patronales: ", e);
		} catch (Exception e) { // Captura cualquier otra excepción
			logger.error("Error inesperado en findAllPageableSwtAsegurado: {}", e.getMessage(), e);
			throw new RuntimeException("Ocurrió un error inesperado al buscar Registros Patronales: " , e);
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
 
		ModelMapper modelMapper = new ModelMapper(); 
		List<DitPatronGeneralDto> ditPatronGeneralDtos = new ArrayList<DitPatronGeneralDto>();
		 for (DitPatronGeneral ditPatronGeneral : ditPatronGenerals) {  
			DitPatronGeneralDto ditPatronGeneralDto = modelMapper.map(ditPatronGeneral, DitPatronGeneralDto.class); 
			ditPatronGeneralDtos.add(ditPatronGeneralDto);  
		 } 
  

		Page<DitPatronGeneralDto> resultado = null;
		int start = (int) pageable.getOffset();
		int end = (start + pageable.getPageSize()) > ditPatronGeneralDtos.size() ? ditPatronGeneralDtos.size() : (start + pageable.getPageSize());
		if (start > end) {
		 start = end;
		}
		 resultado = new PageImpl<>(ditPatronGeneralDtos.subList(start, end), pageable, ditPatronGeneralDtos.size());
		return resultado;
	}




	/*
	 * Metodo de consulta por denominacionRazonSocial
	*/
	@Override
	@Transactional
	public List<DitPatronGeneralDto> findDitPatronGeneralByDenominacionRazonSocial(String denominacionRazonSocial){
		ModelMapper modelMapper = new ModelMapper(); 
		List<DitPatronGeneral> ditPatronGenerals=ditPatronGeneralRepository.findDitPatronGeneralByDenominacionRazonSocial(denominacionRazonSocial); 
		List<DitPatronGeneralDto> ditPatronGeneralDtos = new ArrayList<DitPatronGeneralDto>();

		 for (DitPatronGeneral ditPatronGeneral : ditPatronGenerals) { 
			DitPatronGeneralDto ditPatronGeneralDto = modelMapper.map(ditPatronGeneral, DitPatronGeneralDto.class); 
			ditPatronGeneralDtos.add(ditPatronGeneralDto); 
		 } 

		return ditPatronGeneralDtos;
	}

	/*
	 * Metodo de consulta por rfc
	*/
	@Override
	@Transactional
	public List<DitPatronGeneralDto> findDitPatronGeneralByRfc(String rfc){
		ModelMapper modelMapper = new ModelMapper(); 
		List<DitPatronGeneral> ditPatronGenerals=ditPatronGeneralRepository.findDitPatronGeneralByRfc(rfc); 
		List<DitPatronGeneralDto> ditPatronGeneralDtos = new ArrayList<DitPatronGeneralDto>();

		 for (DitPatronGeneral ditPatronGeneral : ditPatronGenerals) { 
			DitPatronGeneralDto ditPatronGeneralDto = modelMapper.map(ditPatronGeneral, DitPatronGeneralDto.class); 
			ditPatronGeneralDtos.add(ditPatronGeneralDto); 
		 } 

		return ditPatronGeneralDtos;
	}

	/*
	 * Metodo de consulta por registroPatronal
	*/
	@Override
	@Transactional
	public List<DitPatronGeneralDto> findDitPatronGeneralByRegistroPatronal(String registroPatronal){
		ModelMapper modelMapper = new ModelMapper(); 
		List<DitPatronGeneral> ditPatronGenerals=ditPatronGeneralRepository.findDitPatronGeneralByRegistroPatronal(registroPatronal); 
		List<DitPatronGeneralDto> ditPatronGeneralDtos = new ArrayList<DitPatronGeneralDto>();

		 for (DitPatronGeneral ditPatronGeneral : ditPatronGenerals) { 
			DitPatronGeneralDto ditPatronGeneralDto = modelMapper.map(ditPatronGeneral, DitPatronGeneralDto.class); 
			ditPatronGeneralDtos.add(ditPatronGeneralDto); 
		 } 

		return ditPatronGeneralDtos;
	}

 
 

	}
