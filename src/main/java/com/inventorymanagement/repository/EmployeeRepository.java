package com.inventorymanagement.repository;

import com.inventorymanagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Integer> {
    Employee findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Employee> findByCode(String employeeCode);
    Page<Employee> findByIsBlock(Boolean isBlock, Pageable pageable);
}
