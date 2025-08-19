package com.company.auth_service.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.auth_service.config.RsaKeyProperties;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
@RequiredArgsConstructor
public class JwtConfig {
    private final String issuer = "company-auth-app";
    private final long expiration = 3600000; // 1 hour in milliseconds
    private final long refreshExpiration = 86400000; // 24 hours in milliseconds

    private final RsaKeyProperties rsaKeyProperties;

    @Bean
    public RSAKey rsaKey() {
        return new RSAKey.Builder(rsaKeyProperties.getPublicKey())
            .privateKey(rsaKeyProperties.getPrivateKey())
            .keyID("auth-service-key-" + System.currentTimeMillis()) // ID unique
            .keyUse(KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS256)
            .build();
    }

}
