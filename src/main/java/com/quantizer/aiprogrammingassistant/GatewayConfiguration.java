package com.quantizer.aiprogrammingassistant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;



@Configuration
public class GatewayConfiguration {

    @Value("${microservice1.url}")
    private String microservice1Url;

    @Value("${microservice2.url}")
    private String microservice2Url;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("ms1", r -> r.path("/api/ms1/**")
                        .filters(f -> f.rewritePath("/api/ms1/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Forwarded-Host", "localhost:8080")
                                .addRequestHeader("X-Forwarded-Proto", "http"))
                        .uri(microservice1Url))
                .route("ms2", r -> r.path("/api/ms2/**")
                        .filters(f -> f.rewritePath("/api/ms2/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Forwarded-Host", "localhost:8080")
                                .addRequestHeader("X-Forwarded-Proto", "http"))
                        .uri(microservice2Url))
                .build();
    }
}

