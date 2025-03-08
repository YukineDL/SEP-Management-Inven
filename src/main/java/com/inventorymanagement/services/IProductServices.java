package com.inventorymanagement.services;

import com.inventorymanagement.dto.ProductCreateDTO;
import com.inventorymanagement.dto.ProductDTO;
import com.inventorymanagement.dto.ProductSearchDTO;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductServices {
    void createProduct(String authHeader, ProductCreateDTO product) throws InventoryException;
    Page<ProductDTO> findAllBySearchRequest( ProductSearchDTO searchDTO, Pageable pageable) throws InventoryException;
}
