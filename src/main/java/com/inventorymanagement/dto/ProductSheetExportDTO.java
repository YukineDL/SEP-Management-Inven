package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSheetExportDTO {
    private String productCode;
    private String productStatus;
    private Double totalExportPrice;
    private String keyMap;

    public void createKeyMap() {
        this.keyMap = this.productCode + "-" + productStatus;
    }
}
