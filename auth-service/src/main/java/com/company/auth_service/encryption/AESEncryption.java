package com.company.auth_service.encryption;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class AESEncryption {
    private static final String ALGORITHM = "AES";
    private static byte[] secretKey;

    @Value("${AES.SECRET}")  
    private String SECRET;

    @PostConstruct
    private void init() {
        try {
            // Normalize secret to 256-bit (32 bytes) using SHA-256
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(SECRET.getBytes(StandardCharsets.UTF_8));
            secretKey = Arrays.copyOf(keyBytes, 32); // ensure 256-bit key
        } catch (Exception e) {
            throw new RuntimeException("Error initializing AES key", e);
        }
    }

    public static String encrypt(String value) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting: " + e.getMessage(), e);
        }
    }

    public static String decrypt(String encrypted) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)), StandardCharsets.UTF_8);
        } catch (Exception  e) {
            throw new RuntimeException("Error while decrypting: " + e.getMessage(), e);
        }
    }

}   
