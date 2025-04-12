package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.CategoryDTO;
import com.inventorymanagement.entity.Category;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.CategoryRepository;
import com.inventorymanagement.services.ICategoryServices;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryServicesImpl implements ICategoryServices {
    private final CategoryRepository categoryRepository;
    private final IEmployeeServices employeeServices;
    @Override
    public void create(String authHeader, CategoryDTO categoryDTO) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(!Constants.LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        if(!validateDTO(categoryDTO)){
            throw new InventoryException(
                    ExceptionMessage.CATEGORY_NAME_EMPTY,
                    ExceptionMessage.messages.get(ExceptionMessage.CATEGORY_NAME_EMPTY)
            );
        }
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .code(Utils.convertToCode(categoryDTO.getName()))
                .build();
        if(categoryRepository.existsByCode(category.getCode())){
            throw new InventoryException(
                    ExceptionMessage.CATEGORY_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.CATEGORY_EXISTED)
            );
        }
        categoryRepository.save(category);
    }

    @Override
    public void update(String authHeader, CategoryDTO categoryDTO, String categoryCode) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(!Constants.LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        if(!validateDTO(categoryDTO)){
            throw new InventoryException(
                    ExceptionMessage.CATEGORY_NAME_EMPTY,
                    ExceptionMessage.messages.get(ExceptionMessage.CATEGORY_NAME_EMPTY)
            );
        }
        String code = Utils.convertToCode(categoryDTO.getName());
        if(categoryRepository.existsByCodeAndCodeNotLike(code, categoryCode)){
            throw new InventoryException(
                    ExceptionMessage.CATEGORY_NAME_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.CATEGORY_NAME_EXISTED)
            );
        }
        Category category = findByCode(categoryCode);
        category.setName(categoryDTO.getName());
        category.setCode(code);
        categoryRepository.save(category);
    }

    @Override
    public Page<Category> getAll(Pageable pageable) {
        var content = categoryRepository.findAll(pageable);
        return new PageImpl<>(content.getContent(), pageable, content.getTotalElements());
    }

    @Override
    public Category findByCode(String code) throws InventoryException {
        Optional<Category> categoryOp = categoryRepository.findByCode(code);
        if(categoryOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.CATEGORY_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.CATEGORY_NOT_EXISTED)
            );
        }
        return categoryOp.get();
    }

    private boolean validateDTO(CategoryDTO categoryDTO){
        return categoryDTO.getName() != null && !categoryDTO.getName().isEmpty();
    }
}
