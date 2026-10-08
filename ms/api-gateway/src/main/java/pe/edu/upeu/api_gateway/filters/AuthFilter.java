package pe.edu.upeu.api_gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import pe.edu.upeu.api_gateway.dtos.TokenDto;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GatewayFilter {

    private final WebClient.Builder webClientBuilder;

    // Cambiamos 'ms-auth:3030' por el nombre en Eureka: 'auth-server' (sin puertos)
    private static final String AUTH_VALIDATE_URI = "http://auth-server/auth-server/auth/jwt";
    private static final String ACCESS_TOKEN_HEADER_NAME = "accessToken";

    // Inyectamos el WebClient.Builder administrado por Spring (el que tiene @LoadBalanced)
    public AuthFilter(WebClient.Builder webClientBuilder){
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
            return this.onError(exchange, HttpStatus.UNAUTHORIZED); // Cambiado a 401 por semántica
        }
        final var tokenHeader = exchange
                .getRequest()
                .getHeaders()
                .get(HttpHeaders.AUTHORIZATION).get(0);
        final var chunks = tokenHeader.split(" ");
        if (chunks.length != 2 || !chunks[0].equals("Bearer")){
            return this.onError(exchange, HttpStatus.UNAUTHORIZED);
        }
        final var token = chunks[1];

        // Construimos el WebClient usando el builder inyectado para que aplique el LoadBalancer
        return this.webClientBuilder.build()
                .post()
                .uri(AUTH_VALIDATE_URI)
                .header(ACCESS_TOKEN_HEADER_NAME, token)
                .retrieve()
                .bodyToMono(TokenDto.class)
                .map(response -> exchange)
                .flatMap(chain::filter)
                // Es buena práctica manejar un posible error 401/403 del ms-auth aquí
                .onErrorResume(error -> this.onError(exchange, HttpStatus.UNAUTHORIZED));
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status){
        final var response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
}