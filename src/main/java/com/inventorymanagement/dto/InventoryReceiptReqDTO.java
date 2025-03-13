package com.inventorymanagement.dto;

import com.inventorymanagement.dto.response.BatchNumberDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryReceiptReqDTO {
    private LocalDate accountingDate;
    private LocalDate documentDate;
    private String numberOfReceipts;
    private List<BatchNumberDTO> batchNumbers;
    private String purchaseOrderCode;
    private String employeeCode;
}
