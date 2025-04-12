package com.inventorymanagement.controller;

import com.inventorymanagement.dto.SupplierDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.ISupplierServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/supplier")
public class SupplierController {
    private final ISupplierServices supplierServices;
    @PostMapping(value = "/create")
    public ResponseEntity<Object> create(@RequestBody SupplierDTO dto){
        try {
            supplierServices.create(dto);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (InventoryException inventoryException){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .code(inventoryException.getCode())
                            .message(inventoryException.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
    @PutMapping(value = "/{id}/update")
    public ResponseEntity<Object> updateById(@PathVariable Integer id,
                                             @RequestBody SupplierDTO dto){
        try {
            supplierServices.update(dto,id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (InventoryException inventoryException){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .code(inventoryException.getCode())
                            .message(inventoryException.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
    @GetMapping(value = "/find-all")
    public ResponseEntity<Object> findAll(@RequestParam(name = "page", defaultValue = "0") int page,
                                          @RequestParam(name = "size", defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        return new ResponseEntity<>(
                supplierServices.findAll(pageable),
                HttpStatus.OK
        );
    }
    @PutMapping(value = "/{id}/delete")
    public ResponseEntity<Object> deleteById(@PathVariable Integer id,
                                             @RequestParam Boolean isDeleted){
        try {
            supplierServices.deleteById(id,isDeleted);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> findById(@PathVariable Integer id){
        try {
            return new ResponseEntity<>(
                    supplierServices.findById(id),
                    HttpStatus.OK
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
