package com.inventorymanagement.controller;

import com.inventorymanagement.constant.Constants;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/employees")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {
    private final IEmployeeServices employeeServices;
    @GetMapping
    public ApiResponse<Object> findAll(HttpServletRequest request,
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
            return ApiResponse.builder()
                    .result(employeeServices.getAll(authHeader,pageable,employeeSearchDTO))
                    .build();
        } catch (InventoryException exception){
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST)
                    .message(exception.getMessage())
                    .build();
        }
    }
    @GetMapping(value = "/{employeeCode}")
    public ApiResponse<Object> findByCode(@PathVariable String employeeCode,
                                          HttpServletRequest request){
        try {
            String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
            return ApiResponse.builder()
                    .result(employeeServices.findByCode(authHeader,employeeCode))
                    .build();
        } catch (InventoryException exception){
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST)
                    .message(exception.getMessage())
                    .build();
        }
    }
    @PutMapping(value = "/{employeeCode}")
    public ApiResponse<Object> updateByCode(@PathVariable String employeeCode,
                                            HttpServletRequest request,
                                            @RequestBody EmployeeUpdateDTO employeeUpdateDTO){
        try {
            String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
            employeeServices.updateByCode(authHeader,employeeCode,employeeUpdateDTO);
            return ApiResponse.builder()
                    .code(HttpStatus.ACCEPTED)
                    .build();
        } catch (InventoryException exception){
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST)
                    .message(exception.getMessage())
                    .build();
        }
    }
    @GetMapping(value = "/{employeeCode}/lock")
    public ApiResponse<Object> lock(@PathVariable String employeeCode,
                                    HttpServletRequest request){
       try {
           String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
           employeeServices.lockAccount(authHeader,employeeCode);
           return ApiResponse.builder()
                   .code(HttpStatus.ACCEPTED)
                   .build();
       } catch (InventoryException exception){
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST)
                    .message(exception.getMessage())
                    .build();
       }
    }
    @GetMapping(value = "/{employeeCode}/unlock")
    public ApiResponse<Object> unlock(@PathVariable String employeeCode,
                                    HttpServletRequest request){
        try {
            String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
            employeeServices.unlockAccount(authHeader,employeeCode);
            return ApiResponse.builder()
                    .code(HttpStatus.ACCEPTED)
                    .build();
        } catch (InventoryException exception){
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST)
                    .message(exception.getMessage())
                    .build();
        }
    }
}
