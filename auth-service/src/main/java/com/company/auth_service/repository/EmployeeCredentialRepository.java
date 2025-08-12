package com.company.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.company.auth_service.entity.EmployeeCredential;

@Repository
public interface EmployeeCredentialRepository extends JpaRepository<EmployeeCredential, Long> {
    Optional<EmployeeCredential> findByEmail(String email);
    Boolean existsByEmail(String email);
}
