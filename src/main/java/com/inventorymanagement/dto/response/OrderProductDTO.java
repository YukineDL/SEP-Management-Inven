package com.inventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderProductDTO {
    private String productCode;
    private String productName;
    private String unit;
    private Integer quantity;
    private Double sellingPrice;
}
