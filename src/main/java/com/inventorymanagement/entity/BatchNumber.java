package com.inventorymanagement.entity;

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
    @Column(name = "status")
    private String status;
    @Column(name = "createAt")
    private LocalDate createAt;
}
