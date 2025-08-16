package com.company.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;

@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties
public class GatewayConfig {
     @Value("${auth.service.url}")
    private String authServiceUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, 
                                          JwtAuthenticationFilter jwtFilter) {
        return builder.routes()
            // Public routes - No authentication required
            .route("public-route", r -> r.path("/api/public/**")
                .uri("${user.service.url}"))
            
            .route("auth-route", r -> r.path("/api/auth/**")
                .uri(authServiceUrl))
            
            // User routes - USER or ADMIN role required
            .route("user-route", r -> r.path("/api/user/**")
                .filters(f -> f.filter(jwtFilter.apply(new Config("USER", "ADMIN"))))
                .uri("${user.service.url}"))
            
            // Admin routes - ADMIN role only
            .route("admin-route", r -> r.path("/api/admin/**")
                .filters(f -> f.filter(jwtFilter.apply(new Config("ADMIN"))))
                .uri("${admin.service.url}"))
            
            .build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder
            .withJwkSetUri(authServiceUrl + "/.well-known/jwks.json")
            .cache(Duration.ofMinutes(5))
            .build();
    }

    // CORS Configuration
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    public static class Config {
        private String[] allowedRoles;
        
        public Config(String... roles) {
            this.allowedRoles = roles;
        }
        
        public String[] getAllowedRoles() {
            return allowedRoles;
        }
    }
}
