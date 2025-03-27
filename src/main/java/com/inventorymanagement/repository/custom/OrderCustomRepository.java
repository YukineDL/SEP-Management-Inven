package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.OrderDTO;
import com.inventorymanagement.dto.OrderSearchReqDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderCustomRepository {
    Page<OrderDTO> findOrderBySearchReq(OrderSearchReqDTO dto, Pageable pageable);
}
