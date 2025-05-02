package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.BrandDTO;
import com.inventorymanagement.entity.Brand;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.BrandRepository;
import com.inventorymanagement.services.IBrandServices;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandServicesImpl implements IBrandServices {

    private final BrandRepository brandRepository;
    private final IEmployeeServices employeeService;

    @Override
    public void create(String authHeader, BrandDTO brandDTO) throws InventoryException {
        Employee me = employeeService.getFullInformation(authHeader);
        if(!Constants.LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        if(brandDTO.getBrandName().isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.BRAND_NAME_EMPTY,
                    ExceptionMessage.messages.get(ExceptionMessage.BRAND_NAME_EMPTY)
            );
        }
        Brand brand = Brand.builder()
                .code(Utils.createCode(brandDTO.getBrandName()))
                .name(brandDTO.getBrandName())
                .isDeleted(false)
                .build();
        if(brandRepository.existsByCode(brand.getCode())){
            throw new InventoryException(
                    ExceptionMessage.BRAND_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.BRAND_EXISTED)
            );
        }
        brandRepository.save(brand);
    }

        @Override
        public void update(String authHeader, BrandDTO brandDTO, String brandCode) throws InventoryException {
            Employee me = employeeService.getFullInformation(authHeader);
            if(!Constants.LIST_MANAGER.contains(me.getRoleCode())){
                throw new InventoryException(
                        ExceptionMessage.NO_PERMISSION,
                        ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
                );
            }

            if(brandDTO.getBrandName().isEmpty()){
                throw new InventoryException(
                        ExceptionMessage.BRAND_NAME_EMPTY,
                        ExceptionMessage.messages.get(ExceptionMessage.BRAND_NAME_EMPTY)
                );
            }
            Optional<Brand> brandOptional = brandRepository.findByCode(brandCode);
            if(brandOptional.isEmpty()){
                throw new InventoryException(
                        ExceptionMessage.BRAND_NOT_EXISTED,
                        ExceptionMessage.messages.get(ExceptionMessage.BRAND_NOT_EXISTED)
                );
            }
            if(BooleanUtils.isTrue(brandRepository.existsByNameAndCodeNotLike(brandDTO.getBrandName(),brandCode))){
                throw new InventoryException(
                        ExceptionMessage.BRAND_NAME_EXISTED,
                        ExceptionMessage.messages.get(ExceptionMessage.BRAND_NAME_EXISTED)
                );
            }


            Brand brand = brandOptional.get();
            brand.setName(brandDTO.getBrandName());

            brandRepository.save(brand);
        }

    @Override
    public Page<Brand> findAll(Pageable pageable)   {
        var content = brandRepository.findByIsDeletedOrIsDeletedIsNull(false,pageable);
        return new PageImpl<>(content.getContent(), pageable, content.getTotalElements());
    }

    @Override
    public void deleteByCode(String code) throws InventoryException {
        var brandOp = this.brandRepository.findByCode(code);
        if(brandOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.BRAND_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.BRAND_NOT_EXISTED)
            );
        }
        var brand = brandOp.get();
        brand.setIsDeleted(true);
        brandRepository.save(brand);
    }
}
