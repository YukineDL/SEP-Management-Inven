package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {
    private String productName;
    private String productDescription;
    private String unit;
    private Double sellingPrice;
    private String categoryCode;
    private String brandCode;
    private Boolean isChangeImage;
}
