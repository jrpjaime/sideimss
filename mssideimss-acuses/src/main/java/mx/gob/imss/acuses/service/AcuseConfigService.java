package mx.gob.imss.acuses.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.annotation.PostConstruct;
import mx.gob.imss.acuses.dto.AcuseConfig;
import mx.gob.imss.acuses.enums.TipoAcuse;

 
import org.springframework.stereotype.Service;
 
import java.util.EnumMap;
import java.util.Map;

@Service
public class AcuseConfigService {
    private static final Logger logger = LogManager.getLogger(AcuseServiceImpl.class);

     private final Map<TipoAcuse, AcuseConfig> configs = new EnumMap<>(TipoAcuse.class);
    
    @PostConstruct
    public void init() {
        // Configuración para ACREDITACION_MEMBRESIA
        AcuseConfig acreditacionMembresiaConfig = new AcuseConfig();
        acreditacionMembresiaConfig.setNomDocumento("AcuseAcreditacionMembresia"); // Nombre final del PDF
        acreditacionMembresiaConfig.setDesVersion("reportes\\contadores\\acreditacionmenbresia\\v202512\\SolicitudAcreditacionContador");
        acreditacionMembresiaConfig.addImagePath("imgLogoImss", "reportes\\contadores\\acreditacionmenbresia\\v202512\\img\\logoImss.jpg");
        acreditacionMembresiaConfig.addImagePath("imgGobiernoRepublica", "reportes\\contadores\\acreditacionmenbresia\\v202512\\img\\gobiernoMexico.png");
        acreditacionMembresiaConfig.addImagePath("imgEscudoNacional", "reportes\\contadores\\acreditacionmenbresia\\v202512\\img\\escudoNacional.jpg");
        acreditacionMembresiaConfig.addImagePath("imgGobMx", "reportes\\contadores\\acreditacionmenbresia\\v202512\\img\\gobmx.png");
        acreditacionMembresiaConfig.addImagePath("imgGobMxFooter", "reportes\\contadores\\acreditacionmenbresia\\v202512\\img\\imssGobmx.png");
        acreditacionMembresiaConfig.addImagePath("imgMarcaAgua", "reportes\\contadores\\acreditacionmenbresia\\v202512\\img\\watermark.png");
        configs.put(TipoAcuse.ACREDITACION_MEMBRESIA, acreditacionMembresiaConfig);

        // Configuración para ACUSE_SOLICITUD_CAMBIO (ejemplo)
        AcuseConfig solicitudCambioConfig = new AcuseConfig();
        solicitudCambioConfig.setNomDocumento("AcuseSolicitudCambio");
        solicitudCambioConfig.setDesVersion("reportes\\solicitudes\\cambio\\v202401\\SolicitudCambio");
        solicitudCambioConfig.addImagePath("imgLogoImss", "reportes\\solicitudes\\cambio\\v202401\\img\\logoImss.jpg");
        // ... otros parámetros específicos para ACUSE_SOLICITUD_CAMBIO
        configs.put(TipoAcuse.ACUSE_SOLICITUD_CAMBIO, solicitudCambioConfig);

        // Configuración para ACUSE_SOLICITUD_BAJA (ejemplo)
        AcuseConfig solicitudBajaConfig = new AcuseConfig();
        solicitudBajaConfig.setNomDocumento("AcuseSolicitudBaja");
        solicitudBajaConfig.setDesVersion("reportes\\solicitudes\\baja\\v202401\\SolicitudBaja");
        solicitudBajaConfig.addImagePath("imgLogoImss", "reportes\\solicitudes\\baja\\v202401\\img\\logoImss.jpg");
        // ... otros parámetros específicos para ACUSE_SOLICITUD_BAJA
        configs.put(TipoAcuse.ACUSE_SOLICITUD_BAJA, solicitudBajaConfig);
    }

    public AcuseConfig getConfigForType(TipoAcuse tipoAcuse) {
        return configs.getOrDefault(tipoAcuse, configs.get(TipoAcuse.DEFAULT)); // Retorna DEFAULT si no encuentra
    }
}