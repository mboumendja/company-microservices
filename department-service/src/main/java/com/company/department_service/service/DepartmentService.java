package com.company.department_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.company.department_service.dto.DepartmentRequest;
import com.company.department_service.entity.Department;
import com.company.department_service.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public ResponseEntity<String> createDepartment(DepartmentRequest departmentRequest) {

        try {
            if(departmentRepository.existsByName(departmentRequest.getName())) {
                return ResponseEntity.badRequest().body("DEPARTMENT_NAME_ALREADY_EXIST");
            }

            Department department = Department.builder()
                    .name(departmentRequest.getName())
                    .managerId(Long.parseLong(departmentRequest.getManagerId()))
                    .build();
            
            departmentRepository.save(department);

            return ResponseEntity.accepted().body("DEPARTMENT_CREATED");
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("INVALID_MANAGER_ID_FORMAT");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("UNEXPECTED_SERVER_ERROR");
        }
    }

    public ResponseEntity<List<Department>> getAllDepartment () {
        return ResponseEntity.ok(departmentRepository.findAll());
    }
    
    public ResponseEntity<Optional<Department>> getOneDepartment(Long id) {
        return ResponseEntity.ok(departmentRepository.findById(id));
    }
    
}
