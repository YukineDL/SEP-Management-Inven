package com.inventorymanagement.entity;

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
@Table(name = "purchase_order")
@Builder
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "code")
    private String code;
    @Column(name = "supplier_id")
    private Integer supplierId;
    @Column(name = "employee_code")
    private String employeeCode;
    // Trạng thái đơn hàng
    @Column(name = "delivery_status")
    private String deliveryStatus;
    // Trạng thái duyệt đơn
    @Column(name = "approve")
    private String approve;
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;
    @Column(name = "create_at")
    private LocalDate createAt;
}
