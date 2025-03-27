package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {
    private String employeeCode;
    private Double totalAmount;
    private List<ProductOrderCreateDTO> products;
    private Integer customerId;
}
