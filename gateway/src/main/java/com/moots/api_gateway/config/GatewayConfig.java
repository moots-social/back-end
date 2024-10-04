package com.moots.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
