package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReceiptSearchReq {
    private LocalDate createAt;
    private String employeeCode;
    private String purchaseOrderCode;
    private String statusImport;
    private String approve;
    private String numberOfReceipts;
    private String code;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer supplierId;
}
