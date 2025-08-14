package com.company.auth_service.service;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.company.auth_service.dto.LoginRequest;
import com.company.auth_service.dto.LoginResponse;
import com.company.auth_service.dto.RegisterRequest;
import com.company.auth_service.dto.RegisterResponse;
import com.company.auth_service.entity.EmployeeCredential;
import com.company.auth_service.entity.Role;
import com.company.auth_service.jwt.JwtService;
import com.company.auth_service.repository.EmployeeCredentialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final EmployeeCredentialRepository employeeCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        EmployeeCredential credential = (EmployeeCredential) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(credential);
        
        return LoginResponse.builder()
                    .firstName(credential.getFirstName())
                    .lastName(credential.getLastName())
                    .email(credential.getEmail())
                    .tokenType("Bearer")
                    .accessToken(accessToken)
                    .expiresIn(3600L)
                    .build();
    }

    public ResponseEntity<RegisterResponse> register(RegisterRequest request) {        
        if (employeeCredentialRepository.existsByEmail(request.getEmail())) {
            RegisterResponse response = RegisterResponse.builder()
                    .status(HttpStatus.CONFLICT)
                    .message("EMAIL_ALREADY_EXISTS")
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        boolean roleExists = Arrays.stream(Role.values())
                                .anyMatch(r -> r.name().equalsIgnoreCase(request.getRole()));
        
        if(!roleExists) {
            RegisterResponse response = RegisterResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("INVALID_ROLE")
                        .build();
            return ResponseEntity.badRequest().body(response);
        }

        Role assignedRole = Role.valueOf(request.getRole().toUpperCase());
            
        EmployeeCredential credential = EmployeeCredential.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(assignedRole)
                .build();
        
        employeeCredentialRepository.save(credential);
        
        RegisterResponse response = RegisterResponse.builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("ACCOUNT_CREATED")
                        .build();
        
        return ResponseEntity.accepted().body(response);
    }
    
}
