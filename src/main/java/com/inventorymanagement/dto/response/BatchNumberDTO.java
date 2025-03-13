package com.inventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchNumberDTO {
    private String productCode;
    private LocalDate dateOfManufacture;
    private LocalDate dateOfExpiry;
    private String location;
    private Integer inventoryQuantity;
    private Integer quantityShipped;
    private Double unitPrice;
}
