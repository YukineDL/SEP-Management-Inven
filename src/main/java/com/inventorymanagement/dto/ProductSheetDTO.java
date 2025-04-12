package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSheetDTO {
    private String productCode;
    private String productName;
    private String productUnit;
    private String productUnitName;
    private Long quantityShipped;
    private Double totalImportAmount;
    private String productStatus;
    private Long productExportQuantity;
    private Double exportTotalAmount;
    private String keyMap;
    private Long totalInventoryQuantity;

    public void createKeyMap() {
        this.keyMap = this.productCode + "-" + productStatus;
    }
}
