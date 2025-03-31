package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnProductCreateDTO {
    private String productCode;
    private String reason;
    private String statusProduct;
    private LocalDate dateOfManufacture;
    private LocalDate dateExpired;
    private int quantity;
}
