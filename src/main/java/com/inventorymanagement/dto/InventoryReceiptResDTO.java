package com.inventorymanagement.dto;

import com.inventorymanagement.entity.InventoryReceipt;
import com.inventorymanagement.entity.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReceiptResDTO {
    private InventoryReceipt inventoryReceipt;
    private Supplier supplier;
}
