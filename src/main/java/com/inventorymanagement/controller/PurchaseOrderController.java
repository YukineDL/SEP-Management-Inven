package com.inventorymanagement.controller;

import com.inventorymanagement.constant.PURCHASE_ORDER_APPROVE;
import com.inventorymanagement.dto.PurchaseOrderCreateDTO;
import com.inventorymanagement.dto.PurchaseOrderReqDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IPurchaseOrderServices;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/purchase-order")
public class PurchaseOrderController {
    private final IPurchaseOrderServices purchaseOrderServices;
    @PostMapping(value = "/create")
    public ResponseEntity<Object> createPurchaseOrder(@RequestBody PurchaseOrderCreateDTO dto){
        try {
            purchaseOrderServices.createPurchaseOrder(dto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (InventoryException exception){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .code(exception.getCode())
                            .message(exception.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
    @PutMapping(value = "/{purchaseOrderCode}/approve/{approveStatus}")
    public ResponseEntity<Object> approvePurchaseOrder(@PathVariable String purchaseOrderCode,
                                                       @PathVariable String approveStatus,
                                                       HttpServletRequest request){
        try {
            String authHeader = request.getHeader("Authorization");
            purchaseOrderServices.approvePurchaseOrder(authHeader, purchaseOrderCode,approveStatus);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }catch (InventoryException inventoryException){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .codeMessage(inventoryException.getCodeMessage())
                            .message(inventoryException.getMessage())
                            .build(),HttpStatus.BAD_REQUEST
            );
        }
    }
  
    @GetMapping(value = "/{purchaseOrderCode}")
    public ResponseEntity<Object> getPurchaseOrder(@PathVariable String purchaseOrderCode){
        try {

            return new ResponseEntity<>(purchaseOrderServices.getPurchaseOrder(purchaseOrderCode)
                    ,HttpStatus.OK);
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
    @GetMapping(value = "/status")
    public ResponseEntity<Object> getPurchaseOrderStatus(){
        return new ResponseEntity<>(
                List.of(PURCHASE_ORDER_APPROVE.APPROVED,
                        PURCHASE_ORDER_APPROVE.WAITING,
                        PURCHASE_ORDER_APPROVE.REJECTED)
        , HttpStatus.OK);
    }
    @GetMapping(value = "/find")
    public ResponseEntity<Object> findBySearchRequest(@RequestParam(required = false) String approveStatus,
                                                      @RequestParam(required = false) String deliveryStatus,
                                                      @RequestParam(name = "page", defaultValue = "0") int page,
                                                      @RequestParam(name = "size", defaultValue = "10") int size,
                                                      @RequestParam(required = false) LocalDate createAt){
        try {
            PurchaseOrderReqDTO searchDTO = PurchaseOrderReqDTO.builder()
                    .approveStatus(approveStatus)
                    .deliveryStatus(deliveryStatus)
                    .pageable(PageRequest.of(page,size))
                    .createAt(createAt)
                    .build();
            return new ResponseEntity<>(
                    purchaseOrderServices.findBySearchRequest(searchDTO),
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
    @PutMapping(value = "/{purchaseOrderCode}")
    public ResponseEntity<Object> updatePurchaseOrderCodeByCode(@PathVariable String purchaseOrderCode,
                                                                @RequestBody PurchaseOrderCreateDTO dto){
        try {
            return new ResponseEntity<>(
                    purchaseOrderServices.updatePurchaseOrder(dto, purchaseOrderCode),
                    HttpStatus.ACCEPTED
            );
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
