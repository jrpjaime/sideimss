package mx.gob.imss.acuses.config;

import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

// --- IMPORTS NECESARIOS PARA JASPER ---
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.engine.fonts.SimpleFontExtensionHelper;
import net.sf.jasperreports.extensions.ExtensionsEnvironment;
import java.util.List;
// --------------------------------------

@Configuration
public class JasperReportConfig {

    @PostConstruct
    public void registerFonts() {
        System.out.println(">>> INICIANDO CARGA DE FUENTES JASPER (EXTENSIONES) <<<");
        
        try {
            // 1. Instanciamos el ayudante de Jasper para extensiones de fuentes
            SimpleFontExtensionHelper helper = SimpleFontExtensionHelper.getInstance();
            
            // 2. Cargamos el archivo fonts.xml que creaste previamente
            // Este archivo es CLAVE porque dice: "Cuando pidan Arial Bold, usa arialbd.ttf"
            List<FontFamily> families = helper.loadFontFamilies("fonts.xml");
            
            // 3. Registramos estas familias en el entorno de JasperReports
            ExtensionsEnvironment.getSystemExtensionsRegistry()
                    .getExtensions(FontFamily.class)
                    .addAll(families);
            
            System.out.println(">>> ÉXITO: Se cargaron " + families.size() + " familias de fuentes desde fonts.xml <<<");
            
            // Debug: Imprimir qué familias se cargaron
            for(FontFamily f : families) {
                System.out.println("   - Familia cargada: " + f.getName());
            }

        } catch (Exception e) {
            System.err.println(">>> ERROR CRÍTICO AL CARGAR FONTS.XML <<<");
            System.err.println("Asegúrate de que 'fonts.xml' esté directamente dentro de 'src/main/resources/'");
            e.printStackTrace();
        }
    }
}