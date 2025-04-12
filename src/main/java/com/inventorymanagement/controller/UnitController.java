package com.inventorymanagement.controller;

import com.inventorymanagement.dto.UnitCreateReqDTO;
import com.inventorymanagement.dto.UnitSearchReqDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IUnitServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/unit")
public class UnitController {
    private final IUnitServices unitServices;

    @GetMapping(value = "/{code}")
    public ResponseEntity<Object> getUnit(@PathVariable String code) {
        try {
            return new ResponseEntity<>(unitServices.getUnit(code), HttpStatus.OK);
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
    @PostMapping(value = "/create")
    public ResponseEntity<Object> createUnit(@RequestBody UnitCreateReqDTO dto,
                                              HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            unitServices.createUnit(authHeader,dto);
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
    @PutMapping(value = "/{code}/update")
    public ResponseEntity<Object> updateUnit(@RequestBody UnitCreateReqDTO dto,
                                             HttpServletRequest request,
                                             @PathVariable String code) {
        try {
            String authHeader = request.getHeader("Authorization");
            unitServices.updateUnit(authHeader,dto,code);
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
    @DeleteMapping(value = "/{code}/delete")
    public ResponseEntity<Object> deleteUnit(@PathVariable String code,
                                             HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            unitServices.deleteUnit(authHeader,code);
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
    public ResponseEntity<Object> getAllUnits(
                                              @RequestParam(required = false) Boolean isDeleted,
                                              @RequestParam(required = false) String unitName,
                                              @RequestParam(name = "page", defaultValue = "0")int page,
                                              @RequestParam(name = "size", defaultValue = "10") int size) {
        UnitSearchReqDTO dto = UnitSearchReqDTO.builder()
                .unitName(unitName)
                .isDeleted(isDeleted)
                .build();
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(
                unitServices.getAllUnits(dto,pageable),
                HttpStatus.OK
        );
    }
}
