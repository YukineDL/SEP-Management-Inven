package com.inventorymanagement.controller;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.InventoryDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IInventoryServices;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/inventory")
public class InventoryController {
    private final IInventoryServices inventoryServices;

    @PostMapping(value = "/init")
    public ApiResponse<Object> createInventory(HttpServletRequest request,
                                               @RequestBody InventoryDTO inventoryDTO) {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            inventoryServices.createInventory(inventoryDTO,authHeader);
            return ApiResponse.builder()
                    .code(HttpStatus.OK)
                    .message(Constants.SUCCESS)
                    .build();
        } catch (InventoryException exception){
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST)
                    .message(exception.getMessage())
                    .build();
        }
    }
}
