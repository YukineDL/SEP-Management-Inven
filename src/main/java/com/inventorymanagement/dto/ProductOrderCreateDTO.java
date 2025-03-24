package com.inventorymanagement.dto;
 
 import lombok.AllArgsConstructor;
 import lombok.Data;
 import lombok.NoArgsConstructor;
 
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 public class ProductOrderCreateDTO {
     private String productCode;
     private Integer quantity;
     private float discount;
 }