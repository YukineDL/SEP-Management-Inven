package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductOrderDTO {
    private String code;
    private String name;
    private String unit;
    private int quantity;
    private Long inventoryQuantity;
    private Double sellingPrice;
    private float discount;
}
