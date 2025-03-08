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
@Table(name = "inventory_receipt")
public class InventoryReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "code")
    private String code;
    @Column(name = "purchaseOrderCode")
    private String purchaseOrderCode;
    @Column(name = "employeeCode")
    private String employeeCode;
    @Column(name = "statusImport")
    private String statusImport;
    @Column(name = "createAt")
    private LocalDate createAt;
}
