package com.inventorymanagement.dto;

import com.inventorymanagement.entity.Category;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class ProductCategoryDTO {
    private Category category;
    private List<ProductDTO> products;
}
