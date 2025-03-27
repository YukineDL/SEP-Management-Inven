package com.inventorymanagement.entity;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.response.BatchNumberDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "batch_number")
public class BatchNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "product_code")
    private String productCode;
    @Column(name = "date_of_manufacture")
    private LocalDate dateOfManufacture;
    @Column(name = "date_expired")
    private LocalDate dateExpired;
    @Column(name = "location")
    private String location;
    @Column(name = "inventory_quantity")
    private Integer inventoryQuantity;
    @Column(name = "quantity_shipped")
    private Integer quantityShipped;
    @Column(name = "inventory_receipt_code")
    private String inventoryReceiptCode;
    @Column(name = "status")
    private String status;
    @Column(name = "create_at")
    private LocalDate createAt;
    @Column(name = "export_quantity")
    private Integer exportQuantity;
    @Column(name = "export_quantity_last")
    private Integer exportQuantityLast;
    public BatchNumber(BatchNumberTemp temp, String inventoryReceiptCode) {
        this.productCode = temp.getProductCode();
        this.dateOfManufacture = temp.getDateOfManufacture();
        this.dateExpired = temp.getDateExpired();
        this.location = temp.getLocation();
        this.inventoryQuantity = temp.getInventoryQuantity();
        this.quantityShipped = temp.getQuantityShipped();
        this.inventoryReceiptCode = inventoryReceiptCode;
        this.createAt = LocalDate.now();
        this.status = temp.getStatus();
        this.exportQuantity = 0;
        this.exportQuantityLast = 0;
    }
}
