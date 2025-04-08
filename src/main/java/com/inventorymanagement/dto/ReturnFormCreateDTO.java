package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnFormCreateDTO {
    private String orderCode;
    private String employeeCode;
    private List<ReturnProductCreateDTO> products;
    private Integer customerId;
}
