package com.inventorymanagement.controller;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.CategoryDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.ICategoryServices;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/category")
public class CategoryController {
    private final ICategoryServices categoryServices;

    @PostMapping(value = "/create")
    public ApiResponse<Object> create(HttpServletRequest request,
                                      @RequestBody CategoryDTO categoryDTO) {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            categoryServices.create(authHeader, categoryDTO);
            return ApiResponse.builder()
                    .code(HttpStatus.ACCEPTED)
                    .message(Constants.SUCCESS)
                    .build();
        } catch (InventoryException inventoryException){
            return ApiResponse.builder()
                    .code(inventoryException.getCode())
                    .message(inventoryException.getMessage())
                    .build();
        }
    }
    @PutMapping(value = "/update/{categoryCode}")
    public ApiResponse<Object> updateByCode(HttpServletRequest request,
                                            @RequestBody CategoryDTO categoryDTO, @PathVariable String categoryCode) {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            categoryServices.update(authHeader, categoryDTO, categoryCode);
            return ApiResponse.builder()
                    .code(HttpStatus.ACCEPTED)
                    .message(Constants.SUCCESS)
                    .build();
        } catch (InventoryException exception){
            return ApiResponse.builder()
                    .code(exception.getCode())
                    .message(exception.getMessage())
                    .build();
        }
    }
    @GetMapping(value = "/find-all")
    public ApiResponse<Object> getAll(@RequestParam(name = "page", defaultValue = "0") int page
            , @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.builder()
                .code(HttpStatus.OK)
                .result(categoryServices.getAll(pageable))
                .build();
    }
}
