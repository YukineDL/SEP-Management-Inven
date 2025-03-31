package com.inventorymanagement.dto;

import com.inventorymanagement.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnProductDTO {
    private int id;
    private String productCode;
    private String returnFormCode;
    private int quantityReturn;
    private String reason;
    private String statusProduct;
    private LocalDate dateExpired;
    private LocalDate dateOfManufacture;
    private Product productInformation;
}
