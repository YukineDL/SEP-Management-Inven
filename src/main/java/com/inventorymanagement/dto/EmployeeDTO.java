package com.inventorymanagement.dto;

import com.inventorymanagement.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDTO {
    private String code;
    private String name;
    private String phoneNumber;
    private String username;

    public EmployeeDTO(Employee employee) {
        this.code = employee.getCode();
        this.name = employee.getName();
        this.phoneNumber = employee.getPhoneNumber();
        this.username = employee.getUsername();
    }
}
