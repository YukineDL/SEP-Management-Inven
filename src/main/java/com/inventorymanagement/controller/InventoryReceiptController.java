package com.inventorymanagement.controller;

import com.inventorymanagement.dto.InventoryReceiptReqDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IInventoryReceiptServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/inventory-receipt")
@RequiredArgsConstructor
public class InventoryReceiptController {
    private final IInventoryReceiptServices inventoryReceiptServices;

    @PostMapping(value = "/create")
    public ResponseEntity<Object> createInventoryReceipt(@RequestBody InventoryReceiptReqDTO receiptReqDTO) {
        try {
            return new ResponseEntity<>(
                    inventoryReceiptServices.createReceipt(receiptReqDTO), HttpStatus.CREATED);
        } catch (InventoryException exception){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .codeMessage(exception.getCodeMessage())
                            .message(exception.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
