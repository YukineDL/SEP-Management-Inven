package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.ProductCategoryDTO;
import com.inventorymanagement.dto.ProductCreateDTO;
import com.inventorymanagement.dto.ProductDTO;
import com.inventorymanagement.dto.ProductSearchDTO;
import com.inventorymanagement.entity.*;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.BrandRepository;
import com.inventorymanagement.repository.CategoryRepository;
import com.inventorymanagement.repository.ProcessCheckRepository;
import com.inventorymanagement.repository.ProductRepository;
import com.inventorymanagement.repository.custom.ProductRepositoryCustom;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.services.IProductServices;
import com.inventorymanagement.utils.Base64Utils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServicesImpl implements IProductServices {
    private final ProductRepository productRepository;
    private final IEmployeeServices employeeServices;
    private final ProductRepositoryCustom productRepositoryCustom;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final Base64Utils base64Utils;
    private final CloudinaryServices cloudinaryServices;
    private final ProcessCheckRepository processCheckRepository;

    @Override
    public void createProduct(String authHeader, ProductCreateDTO productDTO) throws InventoryException, IOException {
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
        // get string base 64 image and decode to byte and store to database
        Product product = Product.builder()
                .name(productDTO.getProductName())
                .code(productCode)
                .description(productDTO.getProductDescription())
                .unit(productDTO.getUnit())
                .sellingPrice(productDTO.getSellingPrice())
                .categoryCode(productDTO.getCategoryCode())
                .brandCode(productDTO.getBrandCode())
                .createAt(LocalDateTime.now())
                .createBy(me.getUsername())
                .build();
        String url = cloudinaryServices.uploadImage(productDTO.getImageBase64());
        product.setImagePath(url);
        productRepository.saveAndFlush(product);
    }


    @Override
    public Page<ProductDTO> findAllBySearchRequest(ProductSearchDTO searchDTO, Pageable pageable) throws IOException {
        return productRepositoryCustom.findAllBySearchRequest(searchDTO, pageable);
    }

    @Override
    public void updateProduct(String authHeader, ProductCreateDTO productCreateDTO, String productCode) throws InventoryException, IOException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(!Constants.LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        validateDataDTO(productCreateDTO);
        Optional<Product> productOp = productRepository.findByCode(productCode);
        if(productOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.PRODUCT_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_NOT_EXISTED)
            );
        }
        Product product = productOp.get();
        product.updateProduct(productCreateDTO);
        if(BooleanUtils.isTrue(productCreateDTO.getIsChangeImage())){
            String url = cloudinaryServices.uploadImage(productCreateDTO.getImageBase64());
            product.setImagePath(url);
        }
        productRepository.save(product);
    }

    @Override
    public ProductDTO findByCode(String code) throws InventoryException, IOException {
        Optional<Product> productOP = productRepository.findByCode(code);
        if(productOP.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.PRODUCT_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_NOT_EXISTED)
            );
        }
        // get date for mapping dto
        Map<String, String> brandMapValue = brandRepository.findAll().stream().collect(
                Collectors.toMap(Brand::getCode, Brand::getName));
        Map<String, String> categoryMapValue = categoryRepository.findAll().stream().collect(
                Collectors.toMap(Category::getCode, Category::getName));

        ProductDTO dto = new ProductDTO(productOP.get());
        // set value for dto
        dto.setBrandName(brandMapValue.get(dto.getBrandCode()));
        dto.setCategoryName(categoryMapValue.get(dto.getCategoryCode()));
        return dto;
    }

    @Override
    public Page<ProductDTO> findByCategoryCode(String categoryCode, Pageable pageable) {
        ProductSearchDTO searchDTO = ProductSearchDTO.builder()
                .categoryCode(categoryCode)
                .build();
        return productRepositoryCustom.findAllBySearchRequest(searchDTO,pageable);
    }

    @Override
    public List<ProductCategoryDTO> getProductsDependCategoryCode() throws IOException {
        List<ProductCategoryDTO> results = new ArrayList<>();
        Map<String, List<ProductDTO>> mapCategoryProduct = new HashMap<>();
        Map<String, Category> categoryMapValue = categoryRepository.findAll().stream().collect(
                Collectors.toMap(Category::getCode, category -> category));
        Map<String, Brand> brandMapValue = brandRepository.findAll().stream().collect(
                Collectors.toMap(Brand::getCode, brand -> brand));
        List<Product> content = productRepository.findAll();
        for (Product item : content){
            ProductDTO dto = new ProductDTO(item);
            dto.setBrandName(brandMapValue.get(dto.getBrandCode()).getName());
            dto.setCategoryName(categoryMapValue.get(dto.getCategoryCode()).getName());
            if(mapCategoryProduct.containsKey(item.getCategoryCode())){
                mapCategoryProduct.get(item.getCategoryCode()).add(dto);
            }else{
                List<ProductDTO> values = new ArrayList<>();
                values.add(dto);
                mapCategoryProduct.put(item.getCategoryCode(), values);
            }
        }
        mapCategoryProduct.forEach((key,value) -> {
            ProductCategoryDTO item = ProductCategoryDTO.builder()
                    .category(categoryMapValue.get(key))
                    .products(value)
                    .build();
            results.add(item);
        });
        return results;
    }

    private void validateDataDTO(ProductCreateDTO product) throws InventoryException {
        if(StringUtils.isBlank(product.getProductName())){
            throw new InventoryException(
                    ExceptionMessage.PRODUCT_NAME_EMPTY,
                    ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_NAME_EMPTY)
            );
        }
        if(StringUtils.isBlank(product.getUnit())){
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
        if(StringUtils.isBlank(product.getCategoryCode())){
            throw new InventoryException(
                    ExceptionMessage.PRODUCT_CATEGORY_CODE_EMPTY,
                    ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_CATEGORY_CODE_EMPTY)
            );
        }
        if(StringUtils.isBlank(product.getBrandCode())){
            throw new InventoryException(
                    ExceptionMessage.PRODUCT_BRAND_CODE_EMPTY,
                    ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_BRAND_CODE_EMPTY)
            );
        }
    }
}
