package com.inventorymanagement.dto;

import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.entity.InventoryReceipt;
import com.inventorymanagement.entity.Supplier;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReceiptDTO {
    private String code;

    private String purchaseOrderCode;

    private String statusImport;

    private String approve;

    private LocalDate createAt;

    private LocalDate accountingDate;

    private LocalDate documentDate;

    private String numberOfReceipts;

    private Double totalAmount;

    private String username;

    private LocalDateTime actionTime;

    private LocalDateTime createAtDateTime;

    private Integer totalQuantity;

    private Supplier supplier;
    private Employee employee;

    private List<BatchNumberDTO> items;

    private List<BatchNumberDTO> itemsInInventory;
    public InventoryReceiptDTO(InventoryReceipt inventoryReceipt) {
        this.code = inventoryReceipt.getCode();
        this.purchaseOrderCode = inventoryReceipt.getPurchaseOrderCode();
        this.statusImport = inventoryReceipt.getStatusImport();
        this.approve = inventoryReceipt.getApprove();
        this.createAt = inventoryReceipt.getCreateAt();
        this.accountingDate = inventoryReceipt.getAccountingDate();
        this.documentDate = inventoryReceipt.getDocumentDate();
        this.numberOfReceipts = inventoryReceipt.getNumberOfReceipts();
        this.totalAmount = inventoryReceipt.getTotalAmount();
        this.username = inventoryReceipt.getUsername();
        this.actionTime = inventoryReceipt.getActionTime();
        this.createAtDateTime = inventoryReceipt.getCreateAtDateTime();
        this.totalQuantity = inventoryReceipt.getTotalQuantity();
    }
}
