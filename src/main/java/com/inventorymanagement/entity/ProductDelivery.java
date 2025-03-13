package com.inventorymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_delivery")
public class ProductDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "batch_number_id")
    private Integer batchNumberId;
    @Column(name = "inventory_delivery_code")
    private String inventoryDeliveryCode;
    @Column(name = "export_quantity")
    private Integer exportQuantity;
}
