package com.inventorymanagement.services;

import com.inventorymanagement.dto.CategoryDTO;
import com.inventorymanagement.entity.Category;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryServices {
    void create(String authHeader, CategoryDTO categoryDTO) throws InventoryException;
    void update(String authHeader, CategoryDTO categoryDTO, String categoryCode) throws InventoryException;
    Page<Category> getAll(Pageable pageable);
}
