package com.inventorymanagement.services;

import com.inventorymanagement.dto.OrderCreateDTO;
import com.inventorymanagement.dto.OrderDTO;
import com.inventorymanagement.dto.OrderSearchReqDTO;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface IOrderServices {
    void createOrder(OrderCreateDTO dto, String authHeader) throws InventoryException;
    @Transactional
    void updateOrder(OrderCreateDTO dto, String authHeader, String code) throws InventoryException;
    void approveOrder(String orderCode, String authHeader) throws InventoryException;
    OrderDTO findOrderByCode(String orderCode) throws InventoryException;
    void rejectOrder(String orderCode, String authHeader) throws InventoryException;
    Page<OrderDTO> findBySearchRequest(OrderSearchReqDTO reqDTO, Pageable pageable);
}
