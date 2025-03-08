package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.ProductCreateDTO;
import com.inventorymanagement.dto.ProductDTO;
import com.inventorymanagement.dto.ProductSearchDTO;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.entity.Product;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.ProductRepository;
import com.inventorymanagement.repository.custom.ProductRepositoryCustom;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.services.IProductServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServicesImpl implements IProductServices {
    private final ProductRepository productRepository;
    private final IEmployeeServices employeeServices;
    private final ProductRepositoryCustom productRepositoryCustom;
    @Override
    public void createProduct(String authHeader, ProductCreateDTO productDTO) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(!Constants.LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        validateDataDTO(productDTO);
        String productCode = Constants.PRODUCT_PREFIX_CODE
                + String.format("%05d", productRepository.count() + 1);

        Product product = Product.builder()
                .name(productDTO.getProductName())
                .code(productCode)
                .description(productDTO.getProductDescription())
                .unit(productDTO.getUnit())
                .sellingPrice(productDTO.getSellingPrice())
                .categoryCode(productDTO.getCategoryCode())
                .brandCoded(productDTO.getBrandCode())
                .createAt(LocalDateTime.now())
                .createBy(me.getUsername())
                .build();
        productRepository.save(product);
    }

    @Override
    public Page<ProductDTO> findAllBySearchRequest(ProductSearchDTO searchDTO, Pageable pageable){
        return productRepositoryCustom.findAllBySearchRequest(searchDTO, pageable);
    }

    private void validateDataDTO(ProductCreateDTO product) throws InventoryException {
        if(product.getProductName().isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.PRODUCT_NAME_EMPTY,
                    ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_NAME_EMPTY)
            );
        }
        if(product.getUnit().isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.PRODUCT_UNIT_EMPTY,
                    ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_UNIT_EMPTY)
            );
        }
        if(Objects.isNull(product.getSellingPrice())){
            throw new InventoryException(
                    ExceptionMessage.PRODUCT_SELLING_PRICE_EMPTY,
                    ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_SELLING_PRICE_EMPTY)
            );
        }
        if(product.getCategoryCode().isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.PRODUCT_CATEGORY_CODE_EMPTY,
                    ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_CATEGORY_CODE_EMPTY)
            );
        }
        if(product.getBrandCode().isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.PRODUCT_BRAND_CODE_EMPTY,
                    ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_BRAND_CODE_EMPTY)
            );
        }
    }
}
