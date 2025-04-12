package com.inventorymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "`order`")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "code")
    private String code;
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Column(name = "delivery_status")
    private String deliveryStatus;
    @Column(name = "approve_status")
    private String approveStatus;
    @Column(name = "employee_code")
    private String employeeCode;
    @Column(name = "customer_id")
    private Integer customerId;
    @Column(name = "total_amount")
    private Double totalAmount;
    @Column(name = "approve_by")
    private String approveBy;
    @Column(name = "approve_date")
    private LocalDateTime approveDate;
    @Column(name = "isUsed")
    private Boolean isUsed;
    @Column(name = "delivery_by")
    private String deliveryBy;
    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;
}
