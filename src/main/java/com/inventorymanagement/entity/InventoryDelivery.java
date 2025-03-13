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
@Table(name = "inventory_delivery")
public class InventoryDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "code")
    private String code;
    @Column(name = "receiver")
    private String receiver;
    @Column(name = "reason")
    private String reason;
    @Column(name = "approvedStatus")
    private String approvedStatus;
    @Column(name = "createAt")
    private LocalDate createAt;
}
