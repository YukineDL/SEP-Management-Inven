package com.inventorymanagement.controller;

import com.inventorymanagement.dto.ProductCreateDTO;
import com.inventorymanagement.dto.ProductSearchDTO;
import com.inventorymanagement.dto.response.ApiResponse;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.services.IProductServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping(value = "/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProductController {
    private final IProductServices productServices;
    @PostMapping(value = "/create")
    public ResponseEntity<Object> createProduct(HttpServletRequest request,
                                        @RequestBody ProductCreateDTO productCreateDTO) {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            productServices.createProduct(authHeader, productCreateDTO);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (InventoryException exception){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .message(exception.getMessage())
                            .code(exception.getCode())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping(value = "/find-all")
    public ResponseEntity<Object> findAll(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "categoryCode", required = false) String categoryCode,
            @RequestParam(name = "brandCode", required = false) String brandCode,
            @RequestParam(name = "unitCode", required = false) String unitCode,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ){
        try {
            ProductSearchDTO searchDTO = new ProductSearchDTO(name,code,categoryCode,brandCode, unitCode);
            Pageable pageable = PageRequest.of(page, size);
            return new ResponseEntity<>(
                    productServices.findAllBySearchRequest(searchDTO,pageable),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(ExceptionMessage.messages.get(ExceptionMessage.INTERNAL_SERVER_ERROR))
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    @PutMapping(value = "/update/{productCode}")
    public ResponseEntity<Object> updateByCode(HttpServletRequest request,
                                               @RequestBody ProductCreateDTO dto,
                                               @PathVariable String productCode){
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            productServices.updateProduct(authHeader,dto,productCode);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }catch (InventoryException exception){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .code(exception.getCode())
                            .message(exception.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping(value = "/{productCode}")
    public ResponseEntity<Object> findByCode(@PathVariable String productCode){
        try {
            return new ResponseEntity<>(
                    productServices.findByCode(productCode),
                    HttpStatus.OK
            );
        } catch (InventoryException inventoryException){
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .code(inventoryException.getCode())
                            .message(inventoryException.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        } catch (IOException e) {
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    @GetMapping(value = "/category/{categoryCode}")
    public ResponseEntity<Object> findByCategoryCode(@PathVariable String categoryCode,
                                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                                     @RequestParam(name = "size", defaultValue = "10") int size){
            Pageable pageable = PageRequest.of(page,size);
            return new ResponseEntity<>(
                    productServices.findByCategoryCode(categoryCode,pageable),
                    HttpStatus.OK
            );
    }
    @GetMapping(value = "/get-list-products-category")
    public ResponseEntity<Object> getAllDependCategory() {
       try {
           return new ResponseEntity<>(
                   productServices.getProductsDependCategoryCode(),
                   HttpStatus.OK
           );
       } catch (IOException e){
           return new ResponseEntity<>(
                   e.getMessage(),
                   HttpStatus.INTERNAL_SERVER_ERROR
           );
       }
    }
    @GetMapping(value = "/export-template")
    public ResponseEntity<Object> exportTemplate() {
        try {
            var body = productServices.downloadExcelTemplate();
            ByteArrayResource resource = new ByteArrayResource(body);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=export-template.xlsx")
                    .contentLength(body.length)
                    .body(resource);
        } catch (IOException e){
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
