package com.company.auth_service.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedController {
    
    @GetMapping("/me")
    //@PreAuthorize("hasRole('EMPLOYEE')")
    public String me() {
        return "this is a protected route";
    }
}
