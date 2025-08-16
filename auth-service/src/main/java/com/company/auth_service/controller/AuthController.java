package com.company.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.auth_service.dto.LoginRequest;
import com.company.auth_service.dto.LoginResponse;
import com.company.auth_service.dto.RegisterRequest;
import com.company.auth_service.dto.RegisterResponse;
import com.company.auth_service.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Validated @RequestBody LoginRequest request) {     
        LoginResponse response = authService.login(request);   
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Validated @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping("/me")
    public String me() {
        return "this is a protected route";
    }
    
}
