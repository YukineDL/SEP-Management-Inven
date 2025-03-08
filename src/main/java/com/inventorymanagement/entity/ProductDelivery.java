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
    @Column(name = "batchNumberId")
    private Integer batchNumberId;
    @Column(name = "inventoryDeliveryCode")
    private String inventoryDeliveryCode;
    @Column(name = "exportQuantity")
    private Integer exportQuantity;
}
