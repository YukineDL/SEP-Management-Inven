package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDeliveryCreateDTO {
    private String taxNumber;
    private int customerId;
    private float taxExportGTGT;
    private Double totalAmount;
}
