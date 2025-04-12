package com.inventorymanagement.controller;

import com.inventorymanagement.dto.InventoryReceiptReqDTO;
import com.inventorymanagement.dto.InventoryReceiptSearchReq;
import com.inventorymanagement.dto.InventoryReturnCreateDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IInventoryReceiptServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/inventory-receipt")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
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
    @PutMapping(value = "/{inventoryReceiptCode}/approve")
    public ResponseEntity<Object> approveInventoryReceipt(@PathVariable String inventoryReceiptCode,
                                                          HttpServletRequest request){
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            inventoryReceiptServices.approveInventoryReceipt(authHeader,inventoryReceiptCode);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
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
    @PutMapping(value = "/{inventoryReceiptCode}/reject")
    public ResponseEntity<Object> rejectInventoryReceipt(@PathVariable String inventoryReceiptCode,
                                                         HttpServletRequest request){
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            inventoryReceiptServices.rejectInventoryReceipt(authHeader, inventoryReceiptCode);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
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
    @GetMapping(value = "/find")
    public ResponseEntity<Object> findInventoryReceipt(@RequestParam(required = false) String employeeCode,
                                                       @RequestParam(required = false) String statusImport,
                                                       @RequestParam(required = false) String approve,
                                                       @RequestParam(required = false) String numberOfReceipts,
                                                       @RequestParam(required = false) LocalDate createAt,
                                                       @RequestParam(name = "page", defaultValue = "0") int page,
                                                       @RequestParam(name = "size", defaultValue = "10") int size,
                                                       @RequestParam(required = false) String code,
                                                       @RequestParam(required = false) LocalDate fromDate,
                                                       @RequestParam(required = false) LocalDate toDate,
                                                       @RequestParam(required = false) Integer supplierId,
                                                       @RequestParam(required = false) Boolean isReturn){
        try {
            InventoryReceiptSearchReq searchReq = InventoryReceiptSearchReq.builder()
                    .employeeCode(employeeCode)
                    .statusImport(statusImport)
                    .approve(approve)
                    .numberOfReceipts(numberOfReceipts)
                    .createAt(createAt)
                    .toDate(toDate)
                    .fromDate(fromDate)
                    .code(code)
                    .supplierId(supplierId)
                    .isReturn(isReturn)
                    .build();
            Pageable pageable = PageRequest.of(page,size);
            return new ResponseEntity<>(
                    inventoryReceiptServices.findBySearchRequest(searchReq, pageable),
                    HttpStatus.OK
            );
        } catch (Exception e){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    @PutMapping(value = "/{inventoryReceiptCode}")
    public ResponseEntity<Object> updateByCode(@PathVariable String inventoryReceiptCode,
                                               @RequestBody InventoryReceiptReqDTO dto){
        try {
            inventoryReceiptServices.updateInventoryReceipt(inventoryReceiptCode,dto);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
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
    @GetMapping(value = "/{inventoryReceiptCode}")
    public ResponseEntity<Object> findByCode(@PathVariable String inventoryReceiptCode){
        try {
            return new ResponseEntity<>(
                    inventoryReceiptServices.findByCode(inventoryReceiptCode),
                    HttpStatus.OK);
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
    @PostMapping(value = "/{returnFormCode}/return-form")
    public ResponseEntity<Object> importReturnForm(@PathVariable String returnFormCode,
                                                   HttpServletRequest request,
                                                   @RequestBody InventoryReturnCreateDTO dto){
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            inventoryReceiptServices.importReturnFormInventoryReceipt(authHeader,returnFormCode,dto);
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
