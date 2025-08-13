package com.company.auth_service.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.company.auth_service.dto.LoginRequest;
import com.company.auth_service.dto.LoginResponse;
import com.company.auth_service.dto.RegisterRequest;
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

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // get the user role
        String accessToken = jwtService.generateToken(userDetails);
        
        EmployeeCredential credential = employeeCredentialRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return LoginResponse.builder()
                    .firstName(credential.getFirstName())
                    .lastName(credential.getLastName())
                    .email(credential.getEmail())
                    .tokenType("Bearer")
                    .accessToken(accessToken)
                    .expiresIn(3600L)
                    .build();
    }

    public String register(RegisterRequest request) {        
        if (employeeCredentialRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
            
        EmployeeCredential credential = EmployeeCredential.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.EMPLOYEE)
                .build();
        
        employeeCredentialRepository.save(credential);
        
        return "ACCOUNT_CREATED";
    }
}
