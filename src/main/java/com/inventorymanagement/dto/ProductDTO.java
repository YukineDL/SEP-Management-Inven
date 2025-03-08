package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String code;
    private String name;
    private String description;
    private Double sellingPrice;
    private String categoryCode;
    private String brandCode;
}
