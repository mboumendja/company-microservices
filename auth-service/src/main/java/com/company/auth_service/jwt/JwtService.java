package com.company.auth_service.jwt;

import java.text.ParseException;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.company.auth_service.config.RsaKeyProperties;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final RsaKeyProperties rsaKeyProperties;
    private final JwtConfig jwtConfig;

    public String generateToken(UserDetails userDetails) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());
            
            String authorities = userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.joining(","));
            
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                // we should consider encrupt the emial here before sending it
                    .subject(userDetails.getUsername())
                    .issuer(jwtConfig.getIssuer())
                    .issueTime(now)
                    .expirationTime(expiryDate)
                    .claim("authorities", authorities)
                    .build();
            
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
                    claimsSet
            );
            
            RSASSASigner signer = new RSASSASigner(rsaKeyProperties.getPrivateKey());
            signedJWT.sign(signer);
            
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public String extractUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing JWT token", e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            
            // Verify signature
            RSASSAVerifier verifier = new RSASSAVerifier(rsaKeyProperties.getPublicKey());
            if (!signedJWT.verify(verifier)) {
                return false;
            }
            
            // Check expiration
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime.before(new Date())) {
                return false;
            }
            
            // Check username
            String username = signedJWT.getJWTClaimsSet().getSubject();
            return username.equals(userDetails.getUsername());
            
        } catch (ParseException | JOSEException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expirationTime.before(new Date());
        } catch (ParseException e) {
            return true;
        }
    }

    public boolean isTokenBlackListed(String token) {
        try {
            // the body to check if the token is blacklisted
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}