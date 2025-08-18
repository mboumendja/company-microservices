package com.company.auth_service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

@RestController
public class RsaKeyController {
    @Autowired
    private RSAKey rsaKey;

    @GetMapping("/.well-known/publicKey.json")
    public Map<String, Object> getPublicKey() {
        return new JWKSet(rsaKey.toPublicJWK()).toJSONObject();
    }
    
}
