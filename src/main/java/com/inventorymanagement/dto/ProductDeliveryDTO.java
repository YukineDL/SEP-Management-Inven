package com.inventorymanagement.dto;

import com.inventorymanagement.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDeliveryDTO {
    private String productCode;
    private Long exportQuantity;
    private Double priceExport;
    private Product product;
}
