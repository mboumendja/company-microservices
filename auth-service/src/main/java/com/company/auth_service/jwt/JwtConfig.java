package com.company.auth_service.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private final String issuer = "company-auth-app";
    private final long expiration = 3600000; // 1 hour in milliseconds
    private final long refreshExpiration = 86400000; // 24 hours in milliseconds
}
