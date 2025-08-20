package com.company.employee_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.company.employee_service.client.DepartmentClient;
import com.company.employee_service.dto.DepartmentResponse;
import com.company.employee_service.dto.EmployeeRequest;
import com.company.employee_service.dto.EmployeeResponse;
import com.company.employee_service.entity.Employee;
import com.company.employee_service.entity.Role;
import com.company.employee_service.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentClient departmentClient;

    public ResponseEntity<List<Employee>> getllEmployees() {
        return ResponseEntity.ok().body(employeeRepository.findAll());
    }

    public ResponseEntity<EmployeeResponse> getEmployee(Long id) {

        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));

        DepartmentResponse dept = departmentClient.getDepartmentById(employee.getDepartmentId());

        EmployeeResponse employeeResponse = new EmployeeResponse(employee, dept);
           
        return ResponseEntity.ok(employeeResponse);
    }

    public ResponseEntity<?> createEmployee (EmployeeRequest employeeRequest) {
        if(employeeRepository.existsByEmail(employeeRequest.getEmail())) {
            return ResponseEntity.badRequest().body("EMPLOYEE_EXISTS");
        }

        boolean roleExists = Arrays.stream(Role.values())
                                .anyMatch(r -> r.name().equalsIgnoreCase(employeeRequest.getRole()));
        
        if(!roleExists) {
            return ResponseEntity.badRequest().body("INVALID_ROLE");
        }

        Role assignedRole = Role.valueOf(employeeRequest.getRole().toUpperCase());

        Employee employee = Employee.builder()
                    .firstName(employeeRequest.getFirstName())
                    .lastName(employeeRequest.getLastName())
                    .email(employeeRequest.getEmail())
                    .role(assignedRole)
                    .departmentId(employeeRequest.getDepartmentId())
                    .build();
        
        employeeRepository.save(employee);

        return ResponseEntity.ok(employee);
    }
}
