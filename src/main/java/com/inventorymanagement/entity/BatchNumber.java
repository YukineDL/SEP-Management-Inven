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
    @Column(name = "productCode")
    private String productCode;
    @Column(name = "dateOfManufacture")
    private LocalDate dateOfManufacture;
    @Column(name = "dateExpired")
    private LocalDate dateExpired;
    @Column(name = "location")
    private String location;
    @Column(name = "inventoryQuantity")
    private Integer inventoryQuantity;
    @Column(name = "quantityShipped")
    private Integer quantityShipped;
    @Column(name = "inventoryReceiptCode")
    private String inventoryReceiptCode;
    private String status;
    @Column(name = "create_at")
    private LocalDate createAt;

    @Column(name = "export_quantity")
    private Integer exportQuantity;
    public BatchNumber(BatchNumberTemp temp, String inventoryReceiptCode) {
        this.productCode = temp.getProductCode();
        this.dateOfManufacture = temp.getDateOfManufacture();
        this.inventoryReceiptCode = inventoryReceiptCode;
        this.createAt = LocalDate.now();
        this.status = Constants.BATCH_NUMBER_AVAILABLE;
        this.exportQuantity = 0;
    }
}
