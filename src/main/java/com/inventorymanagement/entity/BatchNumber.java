package com.inventorymanagement.entity;
import com.inventorymanagement.dto.BatchNumberReturnDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(name = "import_price")
    private Double importPrice;
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
        this.importPrice = temp.getUnitPrice();
        this.exportQuantity = 0;
        this.exportQuantityLast = 0;
    }
    public BatchNumber(BatchNumberReturnDTO dto, String inventoryReceiptCode) {
        this.productCode = dto.getProductCode();
        this.dateOfManufacture = dto.getDateOfManufacture();
        this.dateExpired = dto.getDateOfExpiry();
        this.location = dto.getLocation();
        this.inventoryReceiptCode = inventoryReceiptCode;
        this.createAt = LocalDate.now();
        this.exportQuantity = 0;
        this.exportQuantityLast = 0;
        this.status = dto.getStatus();
        this.quantityShipped = dto.getQuantityReturn();
        this.inventoryQuantity = dto.getQuantityReturn();
    }
}
