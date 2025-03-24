package com.inventorymanagement.dto;

import com.inventorymanagement.entity.Customer;
import com.inventorymanagement.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDeliveryDTO {
    private String code;
    private Integer customerId;
    private Customer customer;
    private String approveStatus;
    private String approveBy;
    private LocalDateTime approveDate;
    private String taxNumber;
    private Double totalAmount;
    private Float taxExportGtGt;
    private LocalDateTime createAt;
    private List<ProductDeliveryDTO> products;
    private String employeeCode;
    private Employee employee;
}
