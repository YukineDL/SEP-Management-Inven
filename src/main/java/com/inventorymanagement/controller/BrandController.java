package com.inventorymanagement.controller;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.BrandDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IBrandServices;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/brand")
@RequiredArgsConstructor
public class BrandController {
    private final IBrandServices brandServices;

    @PostMapping(value = "/create")
    public ApiResponse<Object> create(HttpServletRequest request,
                                      @RequestBody BrandDTO brandDTO) {
        try {
            String authHeader = request.getHeader("Authorization");
            brandServices.create(authHeader, brandDTO);
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
    @PutMapping(value = "/update/{brandCode}")
    public ApiResponse<Object> updateByCode(HttpServletRequest request,
                                             @RequestBody BrandDTO brandDTO, @PathVariable String brandCode) {
        try {
            String authHeader = request.getHeader("Authorization");
            brandServices.update(authHeader, brandDTO, brandCode);
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
    @GetMapping(value = "/find-all")
    public ApiResponse<Object> findAll(@RequestParam(name = "page",defaultValue = "0") int page
            , @RequestParam(name = "size",defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.builder()
                .code(HttpStatus.OK)
                .result(brandServices.findAll(pageable))
                .build();
    }
}
