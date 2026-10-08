package pe.edu.upeu.api_gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {

    @Bean
    @LoadBalanced // <--- Esto hace la magia de traducir "AUTH-SERVER" a la IP real
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}