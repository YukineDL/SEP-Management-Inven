package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchNumberReturnDTO {
    private String productCode;
    private LocalDate dateOfManufacture;
    private LocalDate dateOfExpiry;
    private String location;
    private Integer quantityReturn;
    private String status;
}
