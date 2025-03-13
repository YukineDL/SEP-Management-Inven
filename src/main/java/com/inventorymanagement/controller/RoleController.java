package com.inventorymanagement.controller;

import com.inventorymanagement.services.IRoleServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = "/roles")
@RestController
public class RoleController {
    private final IRoleServices roleServices;
    @GetMapping(value = "/find-all")
    public ResponseEntity<Object> findAll(){
        return new ResponseEntity<>(roleServices.findAll(),
                HttpStatus.OK);
    }
}
