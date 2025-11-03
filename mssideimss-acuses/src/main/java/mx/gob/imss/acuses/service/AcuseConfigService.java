package mx.gob.imss.acuses.service;



import jakarta.annotation.PostConstruct;
import mx.gob.imss.acuses.dto.AcuseConfig;
import mx.gob.imss.acuses.enums.TipoAcuse;

 
import org.springframework.stereotype.Service;
 
import java.util.EnumMap;
import java.util.Map;

@Service
public class AcuseConfigService {

     private final Map<TipoAcuse, AcuseConfig> configs = new EnumMap<>(TipoAcuse.class);
    
    /**
     * Inicializa las configuraciones de los diferentes tipos de acuses al arrancar la aplicación.
     * Este método se ejecuta automáticamente después de que el bean AcuseConfigService ha sido construido.
     * <p>
     * Para cada tipo de acuse, se define una instancia de {@link AcuseConfig} que contiene:
     * <ul>
     *     <li>{@code nomDocumento}: El nombre base que se usará para el archivo PDF del acuse final.
     *         Este campo es **obligatorio** para cada configuración.</li>
     *     <li>{@code desVersion}: La ruta relativa al directorio base de reportes,
     *         que apunta al archivo .jasper sin la extensión.
     *         Este campo es **obligatorio** para cada configuración.</li>
     *     <li>{@code imagePaths}: Un mapa de claves y rutas relativas para las imágenes
     *         o cualquier otro parámetro de texto que JasperReports necesite.
     *         Las claves deben coincidir con los nombres de los parámetros definidos en el reporte Jasper.</li>
     * </ul>
     * <p>
     * Es fundamental que {@code nomDocumento} y {@code desVersion} estén presentes en cada configuración
     * para el correcto funcionamiento de la generación de acuses.
     */
    @PostConstruct
    public void init() {

        // Configuración para ACREDITACION_MEMBRESIA
        // Este acuse gestiona la acreditación y membresía de contadores.
        AcuseConfig acreditacionMembresiaConfig = new AcuseConfig();
        acreditacionMembresiaConfig.setNomDocumento("AcuseAcreditacionMembresia"); // Nombre final del PDF generado (ej. AcuseAcreditacionMembresia.pdf)
        acreditacionMembresiaConfig.setDesVersion("reportes\\contadores\\v20251103\\SolicitudAcreditacionContador"); // Ruta relativa del archivo .jasper (ej. SolicitudAcreditacionContador.jasper)
        acreditacionMembresiaConfig.addImagePath("imgLogoImss", "reportes\\contadores\\v20251103\\img\\logoImss.jpg");
        acreditacionMembresiaConfig.addImagePath("imgGobiernoRepublica", "reportes\\contadores\\v20251103\\img\\gobiernoMexico.png");
        acreditacionMembresiaConfig.addImagePath("imgEscudoNacional", "reportes\\contadores\\v20251103\\img\\escudoNacional.jpg");
        acreditacionMembresiaConfig.addImagePath("imgGobMx", "reportes\\contadores\\v20251103\\img\\gobmx.png");
        acreditacionMembresiaConfig.addImagePath("imgGobMxFooter", "reportes\\contadores\\v20251103\\img\\imssGobmx.png");
        acreditacionMembresiaConfig.addImagePath("imgMarcaAgua", "reportes\\contadores\\v20251103\\img\\watermark.png");
        configs.put(TipoAcuse.ACREDITACION_MEMBRESIA, acreditacionMembresiaConfig);


        // Configuración para ACUSE_SOLICITUD_CAMBIO
        // Este acuse corresponde a las solicitudes de cambio de algún tipo.
        AcuseConfig solicitudCambioConfig = new AcuseConfig();
        solicitudCambioConfig.setNomDocumento("AcuseSolicitudCambio"); // Nombre final del PDF
        solicitudCambioConfig.setDesVersion("reportes\\contadores\\v20251103\\ActualizacionDatos"); // Ruta relativa del archivo .jasper
       acreditacionMembresiaConfig.addImagePath("imgLogoImss", "reportes\\contadores\\v20251103\\img\\logoImss.jpg");
        acreditacionMembresiaConfig.addImagePath("imgGobiernoRepublica", "reportes\\contadores\\v20251103\\img\\gobiernoMexico.png");
        acreditacionMembresiaConfig.addImagePath("imgEscudoNacional", "reportes\\contadores\\v20251103\\img\\escudoNacional.jpg");
        acreditacionMembresiaConfig.addImagePath("imgGobMx", "reportes\\contadores\\v20251103\\img\\gobmx.png");
        acreditacionMembresiaConfig.addImagePath("imgGobMxFooter", "reportes\\contadores\\v20251103\\img\\imssGobmx.png");
        acreditacionMembresiaConfig.addImagePath("imgMarcaAgua", "reportes\\contadores\\v20251103\\img\\watermark.png");
        configs.put(TipoAcuse.ACUSE_SOLICITUD_CAMBIO, solicitudCambioConfig);


        // Configuración para ACUSE_SOLICITUD_BAJA
        // Este acuse corresponde a las solicitudes de baja de algún registro o servicio.
        AcuseConfig solicitudBajaConfig = new AcuseConfig();
        solicitudBajaConfig.setNomDocumento("AcuseSolicitudBaja"); // Nombre final del PDF
        solicitudBajaConfig.setDesVersion("reportes\\contadores\\v20251103\\SolicitudBajaContador"); // Ruta relativa del archivo .jasper
       acreditacionMembresiaConfig.addImagePath("imgLogoImss", "reportes\\contadores\\v20251103\\img\\logoImss.jpg");
        acreditacionMembresiaConfig.addImagePath("imgGobiernoRepublica", "reportes\\contadores\\v20251103\\img\\gobiernoMexico.png");
        acreditacionMembresiaConfig.addImagePath("imgEscudoNacional", "reportes\\contadores\\v20251103\\img\\escudoNacional.jpg");
        acreditacionMembresiaConfig.addImagePath("imgGobMx", "reportes\\contadores\\v20251103\\img\\gobmx.png");
        acreditacionMembresiaConfig.addImagePath("imgGobMxFooter", "reportes\\contadores\\v20251103\\img\\imssGobmx.png");
        acreditacionMembresiaConfig.addImagePath("imgMarcaAgua", "reportes\\contadores\\v20251103\\img\\watermark.png");
        configs.put(TipoAcuse.ACUSE_SOLICITUD_BAJA, solicitudBajaConfig);
    }

    /**
     * Obtiene el objeto de configuración {@link AcuseConfig} para un tipo de acuse específico.
     * Si el tipo de acuse no tiene una configuración definida, se intentará retornar
     * la configuración predeterminada (TipoAcuse.DEFAULT).
     *
     * @param tipoAcuse El {@link TipoAcuse} para el cual se desea obtener la configuración.
     * @return Un objeto {@link AcuseConfig} que contiene todos los parámetros necesarios
     *         para generar el acuse. Retorna {@code null} si no se encuentra ninguna
     *         configuración para el tipo dado y tampoco existe una configuración DEFAULT.
     */
    public AcuseConfig getConfigForType(TipoAcuse tipoAcuse) {
        return configs.getOrDefault(tipoAcuse, configs.get(TipoAcuse.DEFAULT)); // Retorna DEFAULT si no encuentra
    }
}