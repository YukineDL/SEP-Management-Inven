package com.inventorymanagement.controller;

import com.inventorymanagement.dto.InventoryDeliveryCreateDTO;
import com.inventorymanagement.dto.InventoryDeliverySearchReqDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IInventoryDeliveryServices;
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
@RequestMapping(value = "/inventory-delivery")
public class InventoryDeliveryController {
    private final IInventoryDeliveryServices inventoryDeliveryServices;
    @PostMapping("/{orderCode}/create/delivery-order")
    public ResponseEntity<Object> createDeliveryOrder(@PathVariable String orderCode,
                                                      @RequestBody InventoryDeliveryCreateDTO deliveryCreateDTO,
                                                      HttpServletRequest request){
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            inventoryDeliveryServices.createInventoryDeliveryByOrderCode(authHeader,orderCode,deliveryCreateDTO);
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
    @GetMapping(value = "/find-all")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String code,
                                          @RequestParam(required = false) String approveStatus,
                                          @RequestParam(required = false)LocalDate fromDate,
                                          @RequestParam(required = false)LocalDate toDate,
                                          @RequestParam(required = false)Double totalAmountTo,
                                          @RequestParam(required = false)Double totalAmountFrom,
                                          @RequestParam(required = false)Integer customerId,
                                          @RequestParam(required = false) String deliveryType,
                                          @RequestParam(name = "page", defaultValue = "0") int page,
                                          @RequestParam(name = "size", defaultValue = "10") int size){
        try {
            Pageable pageable = PageRequest.of(page,size);
            InventoryDeliverySearchReqDTO searchReqDTO = InventoryDeliverySearchReqDTO.builder()
                    .code(code)
                    .totalAmountTo(totalAmountTo)
                    .totalAmountFrom(totalAmountFrom)
                    .fromDate(fromDate)
                    .toDate(toDate)
                    .customerId(customerId)
                    .approveStatus(approveStatus)
                    .deliveryType(deliveryType)
                    .build();
            return new ResponseEntity<>(inventoryDeliveryServices.findBySearchRequest(searchReqDTO,pageable),HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping(value = "/{inventoryDeliveryCode}/approve")
    public ResponseEntity<Object> approveInventoryDelivery(@PathVariable String inventoryDeliveryCode,
                                                           HttpServletRequest request){
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            inventoryDeliveryServices.approveInventoryDelivery(authHeader,inventoryDeliveryCode);
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
    @PutMapping(value = "/{inventoryDeliveryCode}/reject")
    public ResponseEntity<Object> rejectInventoryDelivery(@PathVariable String inventoryDeliveryCode,
                                                           HttpServletRequest request){
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            inventoryDeliveryServices.rejectInventoryDelivery(authHeader,inventoryDeliveryCode);
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
    @GetMapping(value = "/{inventoryDeliveryCode}")
    public ResponseEntity<Object> findByCode(@PathVariable String inventoryDeliveryCode){
        try {
            return new ResponseEntity<>(
                    inventoryDeliveryServices.findByCode(inventoryDeliveryCode),
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
    @PostMapping(value = "/return/{returnFormCode}")
    public ResponseEntity<Object> createDeliveryForReturnForm(@PathVariable String returnFormCode,
                                                              HttpServletRequest request){
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            inventoryDeliveryServices.createInventoryDeliveryReturn(authHeader,returnFormCode);
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
