package com.inventorymanagement.entity;
 
 import jakarta.persistence.*;
 import lombok.AllArgsConstructor;
 import lombok.Data;
 import lombok.NoArgsConstructor;
 
 @Entity
 @NoArgsConstructor
 @AllArgsConstructor
 @Data
 @Table(name = "common_category")
 public class CommonCategory {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private int id;
     @Column(name = "code")
     private String code;
     @Column(name = "parent_code")
     private String parentCode;
     @Column(name = "name")
     private String name;
 }