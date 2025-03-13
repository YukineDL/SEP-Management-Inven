package com.inventorymanagement.dto;

import com.inventorymanagement.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private String code;
    private String name;
    private String unit;
    private String description;
    private Double sellingPrice;
    private String categoryCode;
    private String categoryName;
    private String brandCode;
    private String brandName;
    public ProductDTO(Product product){
        this.code = StringUtils.isEmpty(product.getCode()) ? StringUtils.EMPTY : product.getCode();
        this.name = StringUtils.isEmpty(product.getName()) ? StringUtils.EMPTY : product.getName();
        this.unit = StringUtils.isEmpty(product.getUnit()) ? StringUtils.EMPTY : product.getUnit();
        this.description = StringUtils.isEmpty(product.getDescription()) ? StringUtils.EMPTY : product.getDescription();
        this.sellingPrice = product.getSellingPrice();
        this.categoryCode = StringUtils.isEmpty(product.getCategoryCode()) ? StringUtils.EMPTY : product.getCategoryCode();
        this.brandCode = StringUtils.isEmpty(product.getBrandCode()) ? StringUtils.EMPTY : product.getBrandCode();
    }
}
