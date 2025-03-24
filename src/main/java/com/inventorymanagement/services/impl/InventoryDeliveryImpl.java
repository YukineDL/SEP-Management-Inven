package com.inventorymanagement.services.impl;
 
 import com.inventorymanagement.services.IInventoryDeliveryServices;
 import lombok.RequiredArgsConstructor;
 import org.springframework.stereotype.Service;
 
 @Service
 @RequiredArgsConstructor
 public class InventoryDeliveryImpl implements IInventoryDeliveryServices {
     @Override
     public void createInventoryDeliveryByOrderCode(String authHeader, String orderCode) {
 
     }
 }