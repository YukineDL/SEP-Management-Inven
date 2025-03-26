package com.inventorymanagement.services;

import com.inventorymanagement.dto.OrderCreateDTO;
import com.inventorymanagement.dto.OrderDTO;
import com.inventorymanagement.dto.OrderSearchReqDTO;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@ReRequiredArgsConstructor
public class OrderServicesImpl implements IOrderServices {
    void createOrder(OrderCreateDTO dto, String authHeader) throws InventoryException;
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
    } throws InventoryException;
    OrderDTO findOrderByCode(String orderCode) throws InventoryException;
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
    Page<OrderDTO> findBySearchRequest(OrderSearchReqDTO reqDTO, Pageable pageable);
}
