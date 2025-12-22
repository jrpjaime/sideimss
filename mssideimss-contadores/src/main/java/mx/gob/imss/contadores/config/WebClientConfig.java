package mx.gob.imss.contadores.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // Aquí puedes configurar timeouts globales si lo deseas en el futuro
        return builder.build();
    }
}