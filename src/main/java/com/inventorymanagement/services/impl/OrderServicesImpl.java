package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.constant.PURCHASE_ORDER_APPROVE;
import com.inventorymanagement.constant.RoleEnum;
import com.inventorymanagement.dto.*;
import com.inventorymanagement.entity.*;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.*;
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
import java.util.function.Function;
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
    private final UnitRepository unitRepository;
    private final List<String> LIST_ORDER_ROLE = List.of(
            RoleEnum.SALE.name(),
            RoleEnum.ADMIN.name()
    );
    @Override
    public void createOrder(OrderCreateDTO dto, String authHeader) throws InventoryException {
        Employee me = employeeService.getFullInformation(authHeader);
        if(Objects.isNull(me) || !LIST_ORDER_ROLE.contains(me.getRoleCode())) {
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        String orderCode = this.createCodeOrder();
        Order order = Order.builder()
                .code(orderCode)
                .createAt(LocalDateTime.now())
                .approveStatus(PURCHASE_ORDER_APPROVE.WAITING.name())
                .deliveryStatus(Constants.WAITING_DELIVERY)
                .customerId(dto.getCustomerId())
                .employeeCode(dto.getEmployeeCode())
                .totalAmount(dto.getTotalAmount())
                .isUsed(false)
                .build();
        orderRepository.save(order);
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (ProductOrderCreateDTO item : dto.getProducts()) {
            OrderProduct orderProduct = OrderProduct.builder()
                    .orderCode(orderCode)
                    .productCode(item.getProductCode())
                    .quantity(item.getQuantity())
                    .discount(item.getDiscount())
                    .build();
            orderProducts.add(orderProduct);
        }
        orderProductRepository.saveAll(orderProducts);
    }
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
    public OrderDTO findOrderByCode(String orderCode) throws InventoryException {
        Optional<Order> orderOp = orderRepository.findByCode(orderCode);
        if(orderOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.ORDER_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.ORDER_NOT_EXISTED)
            );
        }
        Map<Integer, Customer> customerMap = customerRepository.findAll().stream().collect(Collectors.toMap(
                Customer::getId, customer -> customer
        ));
        Map<String, Employee> employeeMap = employeeRepository.findAll().stream().collect(Collectors.toMap(
                Employee::getCode, employee -> employee
        ));
        List<ProductOrderDTO> orderProductDTOS = orderProductCustomRepository.findByOrderCode(orderCode);
        var unitMap = unitRepository.findAll().stream().collect(
                Collectors.toMap(Unit::getCode, Unit::getName)
        );
        for (ProductOrderDTO item : orderProductDTOS){
            item.setUnitName(unitMap.get(item.getUnit()));
        }
        Order order = orderOp.get();
        OrderDTO orderDTO = new OrderDTO(order);
        orderDTO.setEmployee(employeeMap.get(order.getEmployeeCode()));
        orderDTO.setCustomer(customerMap.get(order.getCustomerId()));
        orderDTO.setOrderProducts(orderProductDTOS);
        return orderDTO;
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

    @Override
    public Page<OrderDTO> findBySearchRequest(OrderSearchReqDTO reqDTO, Pageable pageable) {
        Page<OrderDTO> content = orderCustomRepository.findOrderBySearchReq(reqDTO,pageable);
        Map<Integer, Customer> customerMap = customerRepository.findAll().stream().collect(Collectors.toMap(
                Customer::getId, customer -> customer
        ));
        Map<String, Employee> employeeMap = employeeRepository.findAll().stream().collect(Collectors.toMap(
                Employee::getCode, employee -> employee
        ));
        for (OrderDTO item : content.getContent()){
            item.setEmployee(employeeMap.get(item.getEmployeeCode()));
            item.setCustomer(customerMap.get(item.getCustomerId()));
        }
        return content;
    }

    @Override
    public void deliveryStatusOrder(String authHeader,String orderCode) throws InventoryException {
        Employee me = employeeService.getFullInformation(authHeader);
        if(me == null || me.getRoleCode().contains(RoleEnum.EMPLOYEE.name())){
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
        var order = orderOp.get();
        if(order.getApproveStatus().equals(PURCHASE_ORDER_APPROVE.REJECTED.name())){
            throw new InventoryException(
                    ExceptionMessage.ORDER_REJECT,
                    ExceptionMessage.messages.get(ExceptionMessage.ORDER_REJECT)
            );
        }
        order.setDeliveryStatus(Constants.RECEIVE_DELIVERY);
        order.setDeliveryBy(me.getName());
        order.setDeliveryDate(LocalDateTime.now());
        orderRepository.save(order);
    }

    private String createCodeOrder(){
        return Constants.ORDER_CODE +
                String.format("%05d", orderRepository.count() + 1);
    }
}
