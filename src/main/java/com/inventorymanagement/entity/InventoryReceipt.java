package com.inventorymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inventory_receipt")
public class InventoryReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "code")
    private String code;
    @Column(name = "purchase_order_code")
    private String purchaseOrderCode;
    @Column(name = "employee_code")
    private String employeeCode;
    @Column(name = "status_import")
    private String statusImport;
    @Column(name = "approve")
    private String approve;
    @Column(name = "create_at")
    private LocalDate createAt;
    @Column(name = "accounting_date")
    private LocalDate accountingDate;
    @Column(name = "document_date")
    private LocalDate documentDate;
    @Column(name = "number_of_receipts")
    private String numberOfReceipts;
    @Column(name = "total_amount")
    private Double totalAmount;
    @Column(name = "username")
    private String username;
    @Column(name = "action_time")
    private LocalDateTime actionTime;
    @Column(name = "create_at_date_time")
    private LocalDateTime createAtDateTime;
    @Column(name = "total_quantity")
    private Integer totalQuantity;
    @Column(name = "supplier_id")
    private Integer supplierId;
    @Column(name = "is_return")
    private Boolean isReturn;
}
