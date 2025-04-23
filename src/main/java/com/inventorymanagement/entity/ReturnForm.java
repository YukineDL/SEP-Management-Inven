package com.inventorymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "return_form")
public class ReturnForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "code")
    private String code;
    @Column(name = "order_code")
    private String orderCode;
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Column(name = "create_by")
    private String createBy;
    @Column(name = "is_used")
    private Boolean isUsed;
    @Column(name = "approve_status")
    private String approveStatus;
    @Column(name = "approve_by")
    private String approveBy;
    @Column(name = "approve_date")
    private LocalDateTime approveDate;
    @Column(name = "total_amount")
    private Double totalAmount;
    @Column(name = "customer_id")
    private Integer customerId;
    @PrePersist
    public void prePersist() {
        if (this.createAt == null) {
            this.createAt = LocalDateTime.now();
        }
    }
}
