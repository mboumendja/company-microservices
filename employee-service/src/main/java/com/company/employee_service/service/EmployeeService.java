package com.company.employee_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.company.employee_service.dto.EmployeeRequest;
import com.company.employee_service.entity.Employee;
import com.company.employee_service.entity.Role;
import com.company.employee_service.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public ResponseEntity<List<Employee>> getllEmployees() {
        return ResponseEntity.ok().body(employeeRepository.findAll());
    }

    public ResponseEntity<Optional<Employee>> getEmployee(Long id) {
        return ResponseEntity.ok(employeeRepository.findById(id));
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
                    .departmentId(Long.valueOf(employeeRequest.getDepartmentId()))
                    .build();
        
        employeeRepository.save(employee);

        return ResponseEntity.ok(employee);
    }
}
