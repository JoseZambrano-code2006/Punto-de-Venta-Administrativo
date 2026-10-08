package pe.edu.upeu.api_gateway.beans;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pe.edu.upeu.api_gateway.filters.AuthFilter;

@Configuration
public class GatewayBeans {
    private final AuthFilter authFilter;

    public GatewayBeans(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    @Profile("eureka-off")
    public RouteLocator routeLocatorEurekaOff(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", route -> route
                        .path("/auth-server/auth/**")
                        .uri("http://localhost:3030")
                )
                .route("clientes-service", route -> route
                        .path("/clientes-service/**")
                        .filters(filter -> {
                            filter.filter(authFilter);
                            return filter;
                        })
                        .uri("http://localhost:8083")
                )
                .route("pos-service", route -> route
                        .path("/pos-venta-service/**")
                        .filters(filter -> {
                            filter.filter(authFilter);
                            return filter;
                        })
                        .uri("http://localhost:8082")
                )
                .build();
    }

    @Bean
    @Profile("eureka-on")
    public RouteLocator routeLocatorEurekaOn(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", route -> route
                        .path("/auth-server/auth/**")
                        .uri("lb://auth-server")
                )
                .route("clientes-service", route -> route
                        .path("/clientes-service/**")
                        .filters(filter -> {
                            filter.filter(authFilter);
                            return filter;
                        })
                        .uri("lb://ms-clientes")
                )
                .route("pos-service", route -> route
                        .path("/pos-venta-service/**")
                        .filters(filter -> {
                            filter.filter(authFilter);
                            return filter;
                        })
                        .uri("lb://pos-service")
                )
                .build();
    }
}
