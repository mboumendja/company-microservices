package com.company.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message= "Email must be in a valid format")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
}
