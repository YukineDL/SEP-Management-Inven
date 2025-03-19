package com.inventorymanagement.services.impl;

import com.inventorymanagement.dto.SupplierDTO;
import com.inventorymanagement.entity.Supplier;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.SupplierRepository;
import com.inventorymanagement.services.ISupplierServices;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierServicesImpl implements ISupplierServices {
    private final SupplierRepository supplierRepository;
    @Override
    public void create(SupplierDTO dto) throws InventoryException {
        validateDTO(dto);
        Supplier supplier = new Supplier(dto);
        supplier.setIsDeleted(false);
        supplierRepository.save(supplier);
    }

   

    @Override
    public Page<Supplier> findAll(Pageable pageable) {
        return supplierRepository.findByIsDeleted(false,pageable);
    }

    @Override
    public void deleteById(Integer id, Boolean isDeleted) throws InventoryException {
        Optional<Supplier> supplierOP = supplierRepository.findById(id);
        if(supplierOP.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.SUPPLIER_NOT_EXIST,
                    ExceptionMessage.messages.get(ExceptionMessage.SUPPLIER_NOT_EXIST)
            );
        }
        Supplier supplier = supplierOP.get();
        supplier.setIsDeleted(isDeleted);
        supplierRepository.save(supplier);
    }

    @Override
    public Supplier findById(Integer id) throws InventoryException {
        Optional<Supplier> supplierOP = supplierRepository.findById(id);
        if(supplierOP.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.SUPPLIER_NOT_EXIST,
                    ExceptionMessage.messages.get(ExceptionMessage.SUPPLIER_NOT_EXIST)
            );
        }
        return supplierOP.get();
    }

    private void validateDTO(SupplierDTO supplierDTO) throws InventoryException {
        if(StringUtils.isBlank(supplierDTO.getName())){
            throw new InventoryException(
                    ExceptionMessage.SUPPLIER_NAME_EMPTY,
                    ExceptionMessage.messages.get(ExceptionMessage.SUPPLIER_NAME_EMPTY)
            );
        }
    }
}
