package com.inventorymanagement.services;
 
 
 public interface IInventoryDeliveryServices {
     void createInventoryDeliveryByOrderCode(String authHeader,String orderCode);
 }