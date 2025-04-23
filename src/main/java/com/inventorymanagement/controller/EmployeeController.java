package com.inventorymanagement.controller;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.EmployeePasswordUpdateDTO;
import com.inventorymanagement.dto.EmployeeSearchDTO;
import com.inventorymanagement.dto.EmployeeUpdateDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IEmployeeServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/employees")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {
    private final IEmployeeServices employeeServices;
    @GetMapping
    public ResponseEntity<Object> findAll(HttpServletRequest request,
                                  @RequestParam(name = "size", defaultValue = "10") int pageSize,
                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "isBlock", required = false) Boolean isBlock,
                                  @RequestParam(name = "name", required = false) String name){
        try {
            String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
            EmployeeSearchDTO employeeSearchDTO = EmployeeSearchDTO.builder()
                    .isBlock(isBlock)
                    .name(name)
                    .build();
            Pageable pageable = PageRequest.of(page,pageSize);
            return new ResponseEntity<>(
                    employeeServices.getAll(authHeader,pageable,employeeSearchDTO),
                    HttpStatus.OK
            );
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
    @GetMapping(value = "/{employeeCode}")
    public ResponseEntity<Object> findByCode(@PathVariable String employeeCode,
                                          HttpServletRequest request){
        try {
            String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
            return new ResponseEntity<>(
                    employeeServices.findByCode(authHeader,employeeCode),
                    HttpStatus.OK
            );
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
    @PutMapping(value = "/{employeeCode}")
    public ResponseEntity<Object> updateByCode(@PathVariable String employeeCode,
                                            HttpServletRequest request,
                                            @RequestBody EmployeeUpdateDTO employeeUpdateDTO){
        try {
            String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
            employeeServices.updateByCode(authHeader,employeeCode,employeeUpdateDTO);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (InventoryException exception){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .message(exception.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
    @GetMapping(value = "/{employeeCode}/lock")
    public ResponseEntity<Object> lock(@PathVariable String employeeCode,
                                    HttpServletRequest request){
       try {
           String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
           employeeServices.lockAccount(authHeader,employeeCode);
           return new ResponseEntity<>(HttpStatus.ACCEPTED);
       } catch (InventoryException exception){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .message(exception.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
       }
    }
    @GetMapping(value = "/{employeeCode}/unlock")
    public ResponseEntity<Object> unlock(@PathVariable String employeeCode,
                                    HttpServletRequest request){
        try {
            String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
            employeeServices.unlockAccount(authHeader,employeeCode);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (InventoryException exception){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .message(exception.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
    @PutMapping(value = "/update-password/{code}")
    public ResponseEntity<Object> updatePassword(@PathVariable String code,
                                                 HttpServletRequest request,
                                                 @RequestBody EmployeePasswordUpdateDTO employeePasswordUpdateDTO
                                                 ){
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            employeeServices.updatePasswordForEmployee(authHeader,code,employeePasswordUpdateDTO);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (InventoryException e){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
