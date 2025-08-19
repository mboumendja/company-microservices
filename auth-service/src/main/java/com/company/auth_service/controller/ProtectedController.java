package com.company.auth_service.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedController {
    
    @GetMapping("/me")
    public String me(Authentication authentication) {
        if(authentication != null) {
            return "user email : " + authentication.getName();
        } else {
            return "error";
        }
    }
}
