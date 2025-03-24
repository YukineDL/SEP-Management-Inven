package com.inventorymanagement.controller;

import com.inventorymanagement.dto.CustomerDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.ICustomerServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final ICustomerServices customerServices;

    @GetMapping(value = "/find-all")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String phoneNumber) {
        return new ResponseEntity<>(
                customerServices.findAllByPhoneNumber(phoneNumber),
                HttpStatus.OK
        );
    }
    @PostMapping(value = "/create")
    public ResponseEntity<Object> create(@RequestBody CustomerDTO customer) {
        try {
            return new ResponseEntity<>(customerServices.createCustomer(customer),HttpStatus.CREATED);
        } catch (InventoryException e){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .codeMessage(e.getCodeMessage())
                            .message(e.getMessage())
                            .build()
                    , HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping(value = "/{id}")
    public ResponseEntity<Object> update(@RequestBody CustomerDTO customer, @PathVariable int id) {
        try {
            customerServices.updateCustomer(customer,id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (InventoryException e){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .codeMessage(e.getCodeMessage())
                            .message(e.getMessage())
                            .build()
                    , HttpStatus.BAD_REQUEST);
        }
    }
}
