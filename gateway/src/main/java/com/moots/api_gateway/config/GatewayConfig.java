package com.moots.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("post_route", r -> r.path("/post/**")
                        .uri("http://api-post:8081"))
                .route("comentario_route", r -> r.path("/comentario/**")
                        .uri("http://api-post:8081"))
                .route("report_route", r -> r.path("/report/**")
                        .uri("http://api-reporte:8082"))
                .route("login_route", r -> r.path("/login")
                        .uri("http://api-usuarios:8083"))
                .route("user_route", r -> r.path("/user/**")
                        .uri("http://api-usuarios:8083"))
                .route("notificacao_route", r -> r.path("/notification/**")
                        .uri("http://api-notificacao:8084"))
                .route("busca_route", r -> r.path("/search/**")
                        .uri("http://api-busca:8085"))
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Especifica explicitamente a origem permitida (em vez de "*")
        config.addAllowedOrigin("http://localhost:3000");  // Permite a origem específica (frontend React)

        config.addAllowedOriginPattern("*"); // Permite todas as origens (use com cuidado em produção)
        config.addAllowedHeader("*"); // Permite todos os cabeçalhos
        config.addAllowedMethod("*"); // Permite todos os métodos (GET, POST, etc.)
        config.setAllowCredentials(true); // Permite envio de cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Aplica a configuração de CORS para todas as rotas

        return new CorsWebFilter(source);
    }
}
