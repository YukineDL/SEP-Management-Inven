package com.inventorymanagement.dto;

import com.inventorymanagement.entity.Customer;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private String code;
    private String deliveryStatus;
    private String approveStatus;
    private LocalDateTime approveDate;
    private String approveBy;
    private Double totalAmount;
    private LocalDateTime createAt;
    private int customerId;
    private String employeeCode;
    private Employee employee;
    private Customer customer;
    private List<ProductOrderDTO> orderProducts;
    public OrderDTO(Order order) {
        this.code = order.getCode();
        this.deliveryStatus = order.getDeliveryStatus();
        this.approveStatus = order.getApproveStatus();
        this.approveDate = order.getApproveDate();
        this.approveBy = order.getApproveBy();
        this.createAt = order.getCreateAt();
        this.employeeCode = order.getEmployeeCode();
        this.customerId = order.getCustomerId();
    }
}