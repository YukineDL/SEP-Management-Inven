package com.inventorymanagement.entity;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.response.BatchNumberDTO;
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
@Table(name = "batch_number_temp")
public class BatchNumberTemp {
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
    @Column(name = "unit_price")
    private Double unitPrice;
    @Column(name = "status")
    private String status;
    public BatchNumberTemp(BatchNumberDTO batchNumberDTO, String inventoryReceiptCode) {
        this.productCode = batchNumberDTO.getProductCode();
        this.dateOfManufacture = batchNumberDTO.getDateOfManufacture();
        this.dateExpired = batchNumberDTO.getDateOfExpiry();
        this.location = batchNumberDTO.getLocation();
        this.quantityShipped = batchNumberDTO.getQuantityShipped();
        this.inventoryQuantity = batchNumberDTO.getQuantityShipped();
        this.inventoryReceiptCode = inventoryReceiptCode;
        this.unitPrice = batchNumberDTO.getUnitPrice();
    }
}
