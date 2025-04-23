package com.inventorymanagement.services;

import com.inventorymanagement.dto.EmployeeDTO;
import com.inventorymanagement.dto.EmployeePasswordUpdateDTO;
import com.inventorymanagement.dto.EmployeeSearchDTO;
import com.inventorymanagement.dto.EmployeeUpdateDTO;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IEmployeeServices {
    Page<Employee> getAll(String authHeader, Pageable pageable, EmployeeSearchDTO searchDTO) throws InventoryException;
    Employee getFullInformation(String authHeader);
    Employee findByCode(String authHeader, String code) throws InventoryException;
    void updateByCode(String authHeader, String employeeCode, EmployeeUpdateDTO employeeUpdateDTO) throws InventoryException;
    void lockAccount(String authHeader, String employeeCode) throws InventoryException;
    void unlockAccount(String authHeader, String employeeCode) throws InventoryException;
    EmployeeDTO findByCode(String code) throws InventoryException;

    void updatePasswordForEmployee(String authHeader, String code, EmployeePasswordUpdateDTO dto) throws InventoryException;
}
