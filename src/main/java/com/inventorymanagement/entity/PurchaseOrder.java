package com.inventorymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @Column(name = "username")
    private String username;
    @Column(name = "action_time")
    private LocalDateTime actionTime;
    @Column(name = "create_at_date_time")
    private LocalDateTime createAtDateTime;
    @Column(name = "delivery_at")
    private LocalDateTime deliveryAt;
    @Column(name = "total_quantity")
    private Integer totalQuantity;
    @Column(name = "is_used")
    private Boolean isUsed;
}
