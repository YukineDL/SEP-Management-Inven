package com.inventorymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchase_order")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "code")
    private String code;
    @Column(name = "supplierId")
    private Integer supplierId;
    @Column(name = "employeeCode")
    private String employeeCode;
    // Trạng thái đơn hàng
    @Column(name = "status")
    private String status;
    // Trạng thái duyệt đơn
    @Column(name = "approve")
    private Boolean approve;
    @Column(name = "deliveryDate")
    private LocalDate deliveryDate;
    @Column(name = "createAt")
    private LocalDateTime createAt;
}
