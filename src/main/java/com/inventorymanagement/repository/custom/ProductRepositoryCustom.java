package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.ProductDTO;
import com.inventorymanagement.dto.ProductSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductDTO> findAllBySearchRequest(ProductSearchDTO productSearchDTO, Pageable pageable);
}
