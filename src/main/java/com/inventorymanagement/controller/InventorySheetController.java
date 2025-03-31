package com.inventorymanagement.controller;

import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IInventorySheetServices;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/inventory-sheet")
public class InventorySheetController {
    private final IInventorySheetServices inventorySheetServices;
    @PostMapping(value = "/sheet")
    public ResponseEntity<Object> createSheetProductByTime(@RequestParam LocalDate startDate,
                                                           @RequestParam LocalDate endDate,
                                                           HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            inventorySheetServices.createInventorySheet(authHeader, startDate, endDate);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (InventoryException e){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .codeMessage(e.getCodeMessage())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
