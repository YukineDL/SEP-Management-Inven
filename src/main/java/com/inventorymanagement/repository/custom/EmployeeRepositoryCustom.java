package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.EmployeeSearchDTO;
import com.inventorymanagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeRepositoryCustom {
    Page<Employee> findAllBySearch(EmployeeSearchDTO searchDTO, Pageable pageable);
}
