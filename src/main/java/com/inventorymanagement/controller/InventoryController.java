package com.inventorymanagement.controller;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.InventoryDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IInventoryServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/inventory")
public class InventoryController {
    private final IInventoryServices inventoryServices;

    @PostMapping(value = "/init")
    public ResponseEntity<Object> createInventory(HttpServletRequest request,
                                          @RequestBody InventoryDTO inventoryDTO) {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            inventoryServices.createInventory(inventoryDTO,authHeader);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (InventoryException exception){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .message(exception.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
