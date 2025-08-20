package com.company.employee_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeRequest {
    @NotBlank(message = "FirstName is required")
    @Size(min = 3, max = 50, message = "Firstname must be between 3 and 50 characters")
    private String firstName;

    @NotBlank(message = "LastName is required")
    @Size(min = 3, max = 50, message = "Lastname must be between 3 and 50 characters")
    private String lastName;

    @Email(message="Email invalid format")
    @NotBlank(message="Email required")
    private String email;

    @NotBlank(message="Role required")
    private String role;

    @NotBlank(message="Department Id required")
    private Long departmentId;
}
