package com.company.employee_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.company.employee_service.dto.DepartmentResponse;

@FeignClient(name ="departmetn-service")
public interface DepartmentClient {

    @GetMapping("/api/departments/{id}")
    DepartmentResponse getDepartmentById(@PathVariable Long id);
}
