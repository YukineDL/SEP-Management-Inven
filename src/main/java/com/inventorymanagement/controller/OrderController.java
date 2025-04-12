package com.inventorymanagement.controller;

import com.inventorymanagement.dto.OrderCreateDTO;
import com.inventorymanagement.dto.OrderSearchReqDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IOrderServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/order")
public class OrderController {
    private final IOrderServices orderServices;

    @GetMapping(value = "/{orderCode}")
    public ResponseEntity<Object> findByCode(@PathVariable("orderCode") String orderCode) {
       try {
            return new ResponseEntity<>(
                    orderServices.findOrderByCode(orderCode), HttpStatus.OK
            );
       } catch (InventoryException e){
           return new ResponseEntity<>(
                   ApiResponse.builder().build(),
                   HttpStatus.BAD_REQUEST
           );
       }
    }
    @PostMapping(value = "/create-order")
    public ResponseEntity<Object> createOrder(@RequestBody OrderCreateDTO dto,
                                              HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            orderServices.createOrder(dto, authHeader);
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
    @PutMapping(value = "/{orderCode}/update")
    public ResponseEntity<Object> updateByCode(@PathVariable String orderCode,
                                               @RequestBody OrderCreateDTO dto,
                                               HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            orderServices.updateOrder(dto, authHeader,orderCode);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (InventoryException e){
            return new ResponseEntity<>(
                    ApiResponse.builder().build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
    @PutMapping(value = "/{orderCode}/approve")
    public ResponseEntity<Object> approveOrder(@PathVariable String orderCode,
                                               HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            orderServices.approveOrder(orderCode,authHeader);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (InventoryException e){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .codeMessage(e.getMessage())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
    @PutMapping(value = "/{orderCode}/reject")
    public ResponseEntity<Object> rejectOrder(@PathVariable String orderCode,
                                              HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            orderServices.rejectOrder(orderCode,authHeader);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (InventoryException e){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .codeMessage(e.getMessage())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
    @GetMapping(value = "/find-all")
    public ResponseEntity<Object> findAllBySearchRequest(@RequestParam(required = false) String code,
                                                         @RequestParam(required = false) String approveStatus,
                                                         @RequestParam(required = false) String deliveryStatus,
                                                         @RequestParam(required = false) String employeeCode,
                                                         @RequestParam(required = false) String phoneNumber,
                                                         @RequestParam(required = false) LocalDate fromDate,
                                                         @RequestParam(required = false) LocalDate toDate,
                                                         @RequestParam(required = false) Double totalAmountTo,
                                                         @RequestParam(required = false) Double totalAmountFrom,
                                                         @RequestParam(name = "page", defaultValue = "0") int page,
                                                         @RequestParam(name = "size", defaultValue = "10")int size){
        OrderSearchReqDTO reqDTO = OrderSearchReqDTO.builder()
                .code(code)
                .customerPhoneNumber(phoneNumber)
                .approveStatus(approveStatus)
                .deliveryStatus(deliveryStatus)
                .employeeCode(employeeCode)
                .fromDate(fromDate)
                .toDate(toDate)
                .totalAmountTo(totalAmountTo)
                .totalAmountFrom(totalAmountFrom)
                .build();
        Pageable pageable = PageRequest.of(page,size);
        return new ResponseEntity<>(orderServices.findBySearchRequest(reqDTO,pageable),HttpStatus.OK);
    }
    @GetMapping(value = "/{orderCode}/delivery-success")
    public ResponseEntity<Object> deliverySuccess(HttpServletRequest request,
                                                  @PathVariable String orderCode) {
        try {
            String authHeader = request.getHeader("Authorization");
            orderServices.deliveryStatusOrder(authHeader,orderCode);
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
