package com.company.department_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DepartmentRequest {

    @NotBlank(message="Department name is required")
    private String name;

    @NotBlank(message="Department manager is required")
    private String managerId;
}
