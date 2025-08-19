package com.company.department_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.department_service.service.DepartmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.department_service.dto.DepartmentRequest;
import com.company.department_service.entity.Department;

@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/")
    public ResponseEntity<String> postMethodName(@Validated @RequestBody DepartmentRequest departmentRequest) {        
        return departmentService.createDepartment(departmentRequest);
    }

    @GetMapping("/")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return departmentService.getAllDepartment();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Department>> getMethodName(@RequestParam Long id) {
        return departmentService.getOneDepartment(id);
    }
}
