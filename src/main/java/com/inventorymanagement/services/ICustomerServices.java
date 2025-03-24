package com.inventorymanagement.services;

import com.inventorymanagement.dto.CustomerDTO;
import com.inventorymanagement.entity.Customer;
import com.inventorymanagement.exception.InventoryException;

import java.util.List;

public interface ICustomerServices {
    Customer createCustomer(CustomerDTO dto) throws InventoryException;
    Customer findById(int id) throws InventoryException;
    List<Customer> findAllByPhoneNumber(String phoneNumber);
    void updateCustomer(CustomerDTO dto, Integer id) throws InventoryException;
}
