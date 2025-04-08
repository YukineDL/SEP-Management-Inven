package com.inventorymanagement.controller;

import com.inventorymanagement.dto.ReturnFormCreateDTO;
import com.inventorymanagement.dto.ReturnFormSearchReq;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IReturnFormServices;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "return-form")
@RequiredArgsConstructor
public class ReturnFormController {
    private final IReturnFormServices returnFormServices;
    @PostMapping(value = "/create")
    public ResponseEntity<Object> createReturnForm(@RequestBody ReturnFormCreateDTO returnFormCreateDTO,
                                                   HttpServletRequest request){
        try {
            String authHeader = request.getHeader("Authorization");
            returnFormServices.createReturnForm(returnFormCreateDTO,authHeader);
            return new ResponseEntity<>(returnFormCreateDTO, HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
    @PutMapping(value = "/{returnFormCode}/approve")
    public ResponseEntity<Object> approveReturnForm(@PathVariable("returnFormCode") String returnFormCode,
                                                    HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        try {
            returnFormServices.approveReturnForm(authHeader,returnFormCode);
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
    @PutMapping(value = "/{returnFormCode}/reject")
    public ResponseEntity<Object> rejectReturnForm(@PathVariable("returnFormCode") String returnFormCode,
                                                   HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            returnFormServices.rejectReturnForm(authHeader, returnFormCode);
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
    @GetMapping(value = "/find-all")
    public ResponseEntity<Object> findAllReturnForm(@RequestParam(name = "page", defaultValue = "0") int page,
                                                    @RequestParam(name = "size", defaultValue = "10") int size,
                                                    @RequestParam(required = false) String code,
                                                    @RequestParam(required = false)LocalDate fromDate,
                                                    @RequestParam(required = false)LocalDate toDate,
                                                    @RequestParam(required = false) Boolean isUsed,
                                                    @RequestParam(required = false) Integer customerId,
                                                    @RequestParam(required = false) Double amountFrom,
                                                    @RequestParam(required = false) Double amountTo,
                                                    HttpServletRequest request) {
       try {
           String authHeader = request.getHeader("Authorization");
           ReturnFormSearchReq dto = ReturnFormSearchReq.builder()
                   .code(code)
                   .toDate(toDate)
                   .fromDate(fromDate)
                   .isUsed(isUsed)
                   .customerId(customerId)
                   .amountFrom(amountFrom)
                   .amountTo(amountTo)
                   .build();
           Pageable pageable = PageRequest.of(page,size);
           return new ResponseEntity<>(returnFormServices.findBySearchRequest(dto, pageable, authHeader),
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
    @GetMapping(value = "/{returnFormCode}")
    public ResponseEntity<Object> findReturnForm(@PathVariable("returnFormCode") String returnFormCode) {
        try {
            return new ResponseEntity<>(
                    returnFormServices.findReturnForm(returnFormCode),
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
}
