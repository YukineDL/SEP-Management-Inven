package com.inventorymanagement.services;

import com.inventorymanagement.dto.ProductCategoryDTO;
import com.inventorymanagement.dto.ProductCreateDTO;
import com.inventorymanagement.dto.ProductDTO;
import com.inventorymanagement.dto.ProductSearchDTO;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IProductServices {
    void createProduct(String authHeader, ProductCreateDTO product) throws InventoryException;
    Page<ProductDTO> findAllBySearchRequest( ProductSearchDTO searchDTO, Pageable pageable) throws InventoryException;
    void updateProduct(String authHeader, ProductCreateDTO productCreateDTO, String productCode) throws InventoryException;
    ProductDTO findByCode(String code) throws InventoryException;
    Page<ProductDTO> findByCategoryCode(String categoryCode, Pageable pageable);
    List<ProductCategoryDTO> getProductsDependCategoryCode();
}
