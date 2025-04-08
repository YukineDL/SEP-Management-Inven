package com.inventorymanagement.controller;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.constant.UnitEnum;
import com.inventorymanagement.dto.CategoryDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.ICategoryServices;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/category")
public class CategoryController {
    private final ICategoryServices categoryServices;

    @PostMapping(value = "/create")
    public ResponseEntity<Object> create(HttpServletRequest request,
                                         @RequestBody CategoryDTO categoryDTO) {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            categoryServices.create(authHeader, categoryDTO);
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
    @PutMapping(value = "/update/{categoryCode}")
    public ResponseEntity<Object> updateByCode(HttpServletRequest request,
                                            @RequestBody CategoryDTO categoryDTO, @PathVariable String categoryCode) {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            categoryServices.update(authHeader, categoryDTO, categoryCode);
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
    @GetMapping(value = "/find-all")
    public ResponseEntity<Object> getAll(@RequestParam(name = "page", defaultValue = "0") int page
            , @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(
                categoryServices.getAll(pageable),
                HttpStatus.OK
        );
    }
    @GetMapping(value = "/find/{categoryCode}")
    public ResponseEntity<Object> findByCode(@PathVariable String categoryCode){
        try {
            return new ResponseEntity<>(
                    categoryServices.findByCode(categoryCode),
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
    @GetMapping(value = "/find/units")
    public ResponseEntity<Object> findAllUnits(){
        var values = Arrays.asList(UnitEnum.values());
        var res = values.stream().map(UnitEnum::getName).toList();
        return new ResponseEntity<>(
                res,
                HttpStatus.OK
        );
    }
}
