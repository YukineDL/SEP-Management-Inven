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
public class BatchNumberDTO {
    private String productCode;
    private String productName;
    private String productUnit;
    private String productUnitName;
    private Double unitPrice;
    private LocalDate dateOfManufacture;
    private LocalDate dateExpired;
    private String location;
    private Integer inventoryQuantity;
    private Integer quantityShipped;
}
