package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryDTO {
    private String productCode;
    private String productName;
    private String unit;
    private Integer quantity;
    private Integer initQuantityProduct;
    private Double price;
}
