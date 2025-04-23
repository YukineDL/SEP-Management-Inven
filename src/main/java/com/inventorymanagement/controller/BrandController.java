package com.inventorymanagement.controller;

import com.inventorymanagement.dto.BrandDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IBrandServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/brand")
@RequiredArgsConstructor
public class BrandController {
    private final IBrandServices brandServices;

    @PostMapping(value = "/create")
    public ResponseEntity<Object> create(HttpServletRequest request,
                                 @RequestBody BrandDTO brandDTO) {
        try {
            String authHeader = request.getHeader("Authorization");
            brandServices.create(authHeader, brandDTO);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
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
    @PutMapping(value = "/update/{brandCode}")
    public ResponseEntity<Object> updateByCode(HttpServletRequest request,
                                             @RequestBody BrandDTO brandDTO, @PathVariable String brandCode) {
        try {
            String authHeader = request.getHeader("Authorization");
            brandServices.update(authHeader, brandDTO, brandCode);
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
    public ResponseEntity<Object> findAll(@RequestParam(name = "page",defaultValue = "0") int page
            , @RequestParam(name = "size",defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(
                brandServices.findAll(pageable),
                HttpStatus.OK
        );
    }
    @GetMapping(value = "/delete/{code}")
    public ResponseEntity<Object> delete(@PathVariable String code) {
        try {
            brandServices.deleteByCode(code);
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
