package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSheetSearchReqDTO {
    private String code;
    private String productName;
    private String productCode;
    private String productUnit;
    private Integer importQuantityProduct;
    private Double importPrice;
    private String productStatus;
    private Integer exportQuantityProduct;
    private Double exportPrice;
}
