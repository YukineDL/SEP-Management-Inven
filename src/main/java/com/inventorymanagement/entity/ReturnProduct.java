package com.inventorymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "return_product")
public class ReturnProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "product_code")
    private String productCode;
    @Column(name = "return_form_code")
    private String returnFormCode;
    @Column(name = "quantity_return")
    private int quantityReturn;
    @Column(name = "reason")
    private String reason;
    @Column(name = "status_product")
    private String statusProduct;
    @Column(name = "date_expired")
    private LocalDate dateExpired;
    @Column(name = "date_of_manufacture")
    private LocalDate dateOfManufacture;
    @Column(name = "discount")
    private Float discount;
}
