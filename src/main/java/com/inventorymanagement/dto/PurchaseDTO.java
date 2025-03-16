package com.inventorymanagement.dto;

import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.entity.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseDTO {
    private String code;
    private Supplier supplier;
    private EmployeeDTO employee;
    private List<ProductPurchaseDTO> products;
    private LocalDate deliveryDate;
    private String deliveryStatus;
    private String approve;
    private LocalDate createAt;
    private LocalDateTime actionTime;
    private String username;
    private LocalDateTime createAtDateTime;
    private LocalDateTime deliveryAt;
    private Integer totalQuantity;
}
