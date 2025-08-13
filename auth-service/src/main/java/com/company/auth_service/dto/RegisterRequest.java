package com.company.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {
    @NotBlank(message = "FirstName is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String firstName;

    @NotBlank(message = "LastName is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

}
