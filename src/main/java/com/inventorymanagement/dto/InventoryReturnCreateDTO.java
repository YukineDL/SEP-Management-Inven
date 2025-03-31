package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReturnCreateDTO {
    private LocalDate accountingDate;
    private LocalDate documentDate;
    private String numberOfReceipts;
    private String employeeCode;
    private Double totalAmount;
    private List<BatchNumberReturnDTO> productReturns;
}
