package com.inventorymanagement.controller;

import com.inventorymanagement.dto.ReturnFormCreateDTO;
import com.inventorymanagement.dto.ReturnFormSearchReq;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IReturnFormServices;
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
    public ResponseEntity<Object> createReturnForm(@RequestBody ReturnFormCreateDTO returnFormCreateDTO) {
        try {
            returnFormServices.createReturnForm(returnFormCreateDTO);
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
    public ResponseEntity<Object> approveReturnForm(@PathVariable("returnFormCode") String returnFormCode) {
        try {
            returnFormServices.approveReturnForm(returnFormCode);
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
    public ResponseEntity<Object> rejectReturnForm(@PathVariable("returnFormCode") String returnFormCode) {
        try {
            returnFormServices.rejectReturnForm(returnFormCode);
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
                                                    @RequestParam(required = false) Boolean isUsed) {
        ReturnFormSearchReq dto = ReturnFormSearchReq.builder()
                .code(code)
                .toDate(toDate)
                .fromDate(fromDate)
                .isUsed(isUsed)
                .build();
        Pageable pageable = PageRequest.of(page,size);
        return new ResponseEntity<>(returnFormServices.findBySearchRequest(dto, pageable),
                HttpStatus.OK);
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
