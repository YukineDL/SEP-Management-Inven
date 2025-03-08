package com.inventorymanagement.controller;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.ProductCreateDTO;
import com.inventorymanagement.dto.ProductSearchDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IProductServices;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductServices productServices;
    @PostMapping(value = "/create")
    public ApiResponse<Object> createProduct(HttpServletRequest request,
                                             @RequestBody ProductCreateDTO productCreateDTO) {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            productServices.createProduct(authHeader, productCreateDTO);
            return ApiResponse.builder()
                    .code(HttpStatus.CREATED)
                    .message(Constants.SUCCESS)
                    .build();
        } catch (InventoryException exception){
            return ApiResponse.builder()
                    .message(exception.getMessage())
                    .code(exception.getCode())
                    .build();
        }
    }
    @GetMapping(value = "/find-all")
    public ApiResponse<Object> findAll(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "categoryCode", required = false) String categoryCode,
            @RequestParam(name = "brandCode", required = false) String brandCode,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ){
        try {
            ProductSearchDTO searchDTO = new ProductSearchDTO(name,code,categoryCode,brandCode);
            Pageable pageable = PageRequest.of(page, size);
            return ApiResponse.builder()
                    .code(HttpStatus.OK)
                    .result(productServices.findAllBySearchRequest(searchDTO,pageable))
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(ExceptionMessage.messages.get(ExceptionMessage.INTERNAL_SERVER_ERROR))
                    .build();
        }
    }
}
