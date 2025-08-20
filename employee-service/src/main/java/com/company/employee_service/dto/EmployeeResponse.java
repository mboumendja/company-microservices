package com.company.employee_service.dto;

import com.company.employee_service.entity.Employee;

public record EmployeeResponse(Employee employee, DepartmentResponse departmentResponse) {
}
