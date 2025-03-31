package com.inventorymanagement.entity;

import com.inventorymanagement.dto.ProductSheetDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_sheet")
public class ProductSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "inventory_sheet_code")
    private String inventorySheetCode;
    @Column(name = "product_code")
    private String productCode;
    @Column(name = "product_name")
    private String productName;
    @Column(name = "product_unit")
    private String productUnit;
    @Column(name = "quantity_shipped")
    private int quantityShipped;
    @Column(name = "total_import_amount")
    private Double totalImportAmount;
    @Column(name = "product_status")
    private String productStatus;
    @Column(name = "product_export_quantity")
    private int productExportQuantity;
    @Column(name = "total_export_amount")
    private Double totalExportAmount;

    public ProductSheet(ProductSheetDTO dto, String code){
        this.inventorySheetCode=code;
        this.productCode=dto.getProductCode();
        this.productName=dto.getProductName();
        this.productUnit=dto.getProductUnit();
        this.quantityShipped= Math.toIntExact(dto.getQuantityShipped());
        this.totalImportAmount=dto.getTotalImportAmount();
        this.productExportQuantity= Math.toIntExact(dto.getProductExportQuantity());
        this.productStatus=dto.getProductStatus();
        this.totalExportAmount = dto.getExportTotalAmount();
    }
}
