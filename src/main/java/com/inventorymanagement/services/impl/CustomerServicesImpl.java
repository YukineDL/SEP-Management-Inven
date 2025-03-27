package com.inventorymanagement.services.impl;

import com.inventorymanagement.dto.CustomerDTO;
import com.inventorymanagement.entity.Customer;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.CustomerRepository;
import com.inventorymanagement.services.ICustomerServices;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServicesImpl implements ICustomerServices {
    private final CustomerRepository customerRepository;
    @Override
    public Customer createCustomer(CustomerDTO dto) throws InventoryException {
        if(customerRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new InventoryException(
                    ExceptionMessage.CUSTOMER_PHONE_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.CUSTOMER_PHONE_EXISTED)
            );
        }
        Customer customer = Customer.builder()
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .build();
        return customerRepository.save(customer);
    }

    @Override
    public Customer findById(int id) throws InventoryException {
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isEmpty()) {
            throw new InventoryException(
                    ExceptionMessage.CUSTOMER_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.CUSTOMER_NOT_EXISTED)
            );
        }
        return customer.get();
    }

    @Override
    public List<Customer> findAllByPhoneNumber(String phoneNumber){
        return StringUtils.isNotEmpty(phoneNumber) ? customerRepository.findByPhoneNumberLike(phoneNumber)
                : customerRepository.findAll();
    }

    @Override
    public void updateCustomer(CustomerDTO dto, Integer id) throws InventoryException {
        Customer customer = this.findById(id);
        customer.updateCustomer(dto);
        customerRepository.save(customer);
    }
}
