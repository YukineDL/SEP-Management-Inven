package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.constant.PURCHASE_ORDER_APPROVE;
import com.inventorymanagement.constant.RoleEnum;
import com.inventorymanagement.dto.*;
import com.inventorymanagement.entity.Customer;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.entity.Order;
import com.inventorymanagement.entity.OrderProduct;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.CustomerRepository;
import com.inventorymanagement.repository.EmployeeRepository;
import com.inventorymanagement.repository.OrderProductRepository;
import com.inventorymanagement.repository.OrderRepository;
import com.inventorymanagement.repository.custom.OrderCustomRepository;
import com.inventorymanagement.repository.custom.OrderProductCustomRepository;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.services.IOrderServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServicesImpl implements IOrderServices {
    private final OrderRepository orderRepository;
    private final IEmployeeServices employeeService;
    private final OrderProductRepository orderProductRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderProductCustomRepository orderProductCustomRepository;
    private final OrderCustomRepository orderCustomRepository;
    private final List<String> LIST_ORDER_ROLE = List.of(
            RoleEnum.SALE.name(),
            RoleEnum.ADMIN.name()
    );
    
    @Transactional
    @Override
    public void updateOrder(OrderCreateDTO dto, String authHeader, String code) throws InventoryException {
        Employee me = employeeService.getFullInformation(authHeader);
        if(Objects.isNull(me) || me.getRoleCode().contains(RoleEnum.SALE.name())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        Optional<Order> orderOp = orderRepository.findByCode(code);
        if(orderOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.ORDER_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.ORDER_NOT_EXISTED)
            );
        }
        Order order = orderOp.get();
        List<String> approveStatus = List.of(PURCHASE_ORDER_APPROVE.REJECTED.name(), PURCHASE_ORDER_APPROVE.APPROVED.name());
        if(approveStatus.contains(order.getApproveStatus())){
            throw new InventoryException(
                    ExceptionMessage.ORDER_APPROVED,
                    ExceptionMessage.messages.get(ExceptionMessage.ORDER_APPROVED)
            );
        }
        order.setTotalAmount(dto.getTotalAmount());
        orderRepository.save(order);
        orderProductRepository.deleteByOrderCode(code);
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (ProductOrderCreateDTO item : dto.getProducts()) {
            OrderProduct orderProduct = OrderProduct.builder()
                    .orderCode(orderOp.get().getCode())
                    .productCode(item.getProductCode())
                    .quantity(item.getQuantity())
                    .discount(item.getDiscount())
                    .build();
            orderProducts.add(orderProduct);
        }
        orderProductRepository.saveAll(orderProducts);
    }

    @Override
    public void approveOrder(String orderCode, String authHeader) throws InventoryException {
        Employee me = employeeService.getFullInformation(authHeader);
        if(me == null || me.getRoleCode().contains(RoleEnum.SALE.name())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        Optional<Order> orderOp = orderRepository.findByCode(orderCode);
        if(orderOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.ORDER_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.ORDER_NOT_EXISTED)
            );
        }
        Order order = orderOp.get();
        if(order.getApproveStatus().equals(PURCHASE_ORDER_APPROVE.REJECTED.name())){
            throw new InventoryException(
                    ExceptionMessage.ORDER_REJECT,
                    ExceptionMessage.messages.get(ExceptionMessage.ORDER_REJECT)
            );
        }
        order.setApproveBy(me.getName());
        order.setApproveStatus(PURCHASE_ORDER_APPROVE.APPROVED.name());
        order.setApproveDate(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    public void rejectOrder(String orderCode, String authHeader) throws InventoryException {
        Employee me = employeeService.getFullInformation(authHeader);
        if(me == null || me.getRoleCode().contains(RoleEnum.SALE.name())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        Optional<Order> orderOp = orderRepository.findByCode(orderCode);
        if(orderOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.ORDER_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.ORDER_NOT_EXISTED)
            );
        }
        Order order = orderOp.get();
        if(order.getApproveStatus().equals(PURCHASE_ORDER_APPROVE.APPROVED.name())){
            throw new InventoryException(
                    ExceptionMessage.ORDER_APPROVED,
                    ExceptionMessage.messages.get(ExceptionMessage.ORDER_APPROVED)
            );
        }
        order.setApproveBy(me.getName());
        order.setApproveStatus(PURCHASE_ORDER_APPROVE.REJECTED.name());
        order.setApproveDate(LocalDateTime.now());
        orderRepository.save(order);
    }
}
