package com.inventorymanagement.dto.response;

import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.entity.PurchaseOrder;
import com.inventorymanagement.entity.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseOrderDTO {
    private String code;
    private String approve;
    private String deliveryStatus;
    private LocalDate deliveryDate;
    private LocalDate createAt;
    private String employeeCode;
    private String name;
    private Integer supplierId;
    private String supplierName;
    private String username;
    private LocalDateTime actionTime;
    private LocalDateTime deliveryAt;
    private LocalDateTime createAtDateTime;
    private Integer totalQuantity;
}
