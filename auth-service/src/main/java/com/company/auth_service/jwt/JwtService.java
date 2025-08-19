package com.company.auth_service.jwt;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.company.auth_service.config.RsaKeyProperties;
import com.company.auth_service.repository.RevokedTokenRepository;
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
    private final RevokedTokenRepository revokedTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    public String generateToken(UserDetails userDetails) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());
            
            String authorities = userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.joining(","));
            
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
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

    /**
     * Extract expiration time (exp claim) from a JWT.
     *
     * @param token the raw JWT string
     * @return Instant expiration time
     * @throws ParseException if JWT cannot be parsed
     */
    public Instant getExpirationTime(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        if (expirationTime == null) {
            throw new IllegalArgumentException("JWT does not contain an expiration claim");
        }

        return expirationTime.toInstant();
    }

    public boolean isTokenBlackListed(String token) {
        try {
            return revokedTokenRepository.existsByToken(token);
        } catch (Exception e) {
            return true;
        }
    }
}