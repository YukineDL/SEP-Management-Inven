package com.inventorymanagement.entity;

import com.inventorymanagement.dto.ProductCreateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "unit_code")
    private String unitCode;
    @Column(name = "sellingPrice")
    private Double sellingPrice;
    @Column(name = "categoryCode")
    private String categoryCode;
    @Column(name = "brandCode")
    private String brandCode;
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Column(name = "create_by")
    private String createBy;
    @Column(name = "image_path")
    private String imagePath;

    public void updateProduct(ProductCreateDTO dto){
        this.name = dto.getProductName();
        this.description = dto.getProductDescription();
        this.brandCode = dto.getBrandCode();
        this.categoryCode = dto.getCategoryCode();
        this.sellingPrice = dto.getSellingPrice();
        this.unitCode = dto.getUnit();
    }
}
