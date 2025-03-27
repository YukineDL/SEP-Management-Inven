package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.ProductOrderDTO;

import java.util.List;

public interface OrderProductCustomRepository {
    List<ProductOrderDTO> findByOrderCode(String orderCode);
}
