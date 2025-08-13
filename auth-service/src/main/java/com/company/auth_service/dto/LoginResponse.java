package com.company.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String tokenType;
    private String accessToken;
    private Long expiresIn;
}

