package com.inventorymanagement.controller;

import com.inventorymanagement.dto.AuthenDTO;
import com.inventorymanagement.dto.RegisterDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IAuthenticatedServices;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/authenticated")
public class AuthenticatedController {
    private final IAuthenticatedServices authenticatedServices;
   
    @PostMapping(value = "/login")
    public ResponseEntity<Object> login(@RequestBody AuthenDTO authenDTO){
            try {
                return new ResponseEntity<>(
                        authenticatedServices.login(authenDTO),
                        HttpStatus.OK
                );
            }catch (InventoryException exception){
                return new ResponseEntity<>(
                        ApiResponse.builder()
                                .code(HttpStatus.BAD_REQUEST)
                                .message(exception.getMessage())
                                .build(),
                        HttpStatus.BAD_REQUEST
                );
            }
    }
}
