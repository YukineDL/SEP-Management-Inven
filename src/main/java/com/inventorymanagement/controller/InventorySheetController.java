package com.inventorymanagement.controller;

import com.inventorymanagement.dto.InventorySheetSearchDTO;
import com.inventorymanagement.dto.ProductSheetSearchReqDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IInventorySheetServices;
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
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/inventory-sheet")
public class InventorySheetController {
    private final IInventorySheetServices inventorySheetServices;
    @PostMapping(value = "/sheet")
    public ResponseEntity<Object> createSheetProductByTime(@RequestParam LocalDate startDate,
                                                           @RequestParam LocalDate endDate,
                                                           @RequestParam String reason,
                                                           HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            return new ResponseEntity<>(
                    inventorySheetServices.createInventorySheet(authHeader, startDate, endDate, reason),
                    HttpStatus.CREATED);
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
    @GetMapping(value = "/{code}")
    public ResponseEntity<Object> getInventorySheetByCode(@PathVariable String code,
                                                          @RequestParam(required = false) String productCode,
                                                          @RequestParam(required = false) String productName,
                                                          @RequestParam(required = false) String productUnit,
                                                          @RequestParam(required = false) String productStatus,
                                                          @RequestParam(required = false) Integer quantityShipped,
                                                          @RequestParam(required = false) Double totalImportAmount,
                                                          @RequestParam(required = false) Integer exportQuantity,
                                                          @RequestParam(required = false) Double totalExport,
                                                          @RequestParam(name = "page", defaultValue = "0") int page,
                                                          @RequestParam(name = "size", defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            ProductSheetSearchReqDTO searchReqDTO = ProductSheetSearchReqDTO.builder()
                    .code(code)
                    .productCode(productCode)
                    .productName(productName)
                    .productUnit(productUnit)
                    .productStatus(productStatus)
                    .importQuantityProduct(quantityShipped)
                    .importPrice(totalImportAmount)
                    .exportQuantityProduct(exportQuantity)
                    .exportPrice(totalExport)
                    .build();
            return new ResponseEntity<>(
                    inventorySheetServices.getDetailInventorySheetBySearchRequest(pageable,searchReqDTO),
                    HttpStatus.OK
            );
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
    @GetMapping(value = "/find-all")
    public ResponseEntity<Object> findAllInventorySheets(@RequestParam(name = "page", defaultValue = "0") int page,
                                                         @RequestParam(name = "size", defaultValue = "10") int size,
                                                         @RequestParam(required = false) LocalDate startDate,
                                                         @RequestParam(required = false) LocalDate endDate,
                                                         @RequestParam(required = false) Boolean isReview) {
        Pageable pageable = PageRequest.of(page, size);
        InventorySheetSearchDTO searchDTO = InventorySheetSearchDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .isReview(isReview)
                .build();
        return new ResponseEntity<>(
                inventorySheetServices.findBySearchRequest(pageable,searchDTO),
                HttpStatus.OK
        );
    }
    @GetMapping(value = "/{code}/export")
    public ResponseEntity<Object> export(@PathVariable String code,
                                                          @RequestParam(required = false) String productCode,
                                                          @RequestParam(required = false) String productName,
                                                          @RequestParam(required = false) String productUnit,
                                                          @RequestParam(required = false) String productStatus,
                                                          @RequestParam(required = false) Integer quantityShipped,
                                                          @RequestParam(required = false) Double totalImportAmount,
                                                          @RequestParam(required = false) Integer exportQuantity,
                                                          @RequestParam(required = false) Double totalExport) {
        try {
            ProductSheetSearchReqDTO searchReqDTO = ProductSheetSearchReqDTO.builder()
                    .code(code)
                    .productCode(productCode)
                    .productName(productName)
                    .productUnit(productUnit)
                    .productStatus(productStatus)
                    .importQuantityProduct(quantityShipped)
                    .importPrice(totalImportAmount)
                    .exportQuantityProduct(exportQuantity)
                    .exportPrice(totalExport)
                    .build();
            return new ResponseEntity<>(
                    inventorySheetServices.exportExcel(searchReqDTO),
                    HttpStatus.OK
            );
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
    @PutMapping(value = "/review/{code}")
    public ResponseEntity<Object> reviewSheet(@PathVariable String code,
                                              HttpServletRequest request){
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            inventorySheetServices.reviewInventorySheet(authHeader,code);
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
}
