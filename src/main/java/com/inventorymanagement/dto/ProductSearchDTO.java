package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductSearchDTO {
    private String name;
    private String code;
    private String categoryCode;
    private String brandCode;
    private String unitCode;
}
