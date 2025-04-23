package com.inventorymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Columns;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inventory_delivery")
public class InventoryDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "code")
    private String code;
    @Column(name = "tax_number")
    private String taxNumber;
    @Column(name = "approve_status")
    private String approveStatus;
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Column(name = "customer_id")
    private Integer customerId;
    @Column(name = "delivery_type")
    private String deliveryType;
    @Column(name = "tax_export_gtgt")
    private float taxExportGtGt;
    @Column(name = "total_amount")
    private Double totalAmount;
    @Column(name = "export_status")
    private String exportStatus;
    @Column(name = "order_code")
    private String orderCode;
    @Column(name = "approve_by")
    private String approveBy;
    @Column(name = "approve_date")
    private LocalDateTime approveDate;
    @Column(name = "employee_code")
    private String employeeCode;
    @Column(name = "return_form_code")
    private String returnFormCode;
}
