package com.inventorymanagement.dto;

import com.inventorymanagement.dto.response.ProductPurchaseOrderDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderCreateDTO {
    // Nhà cung cấp
    @NotNull(message = "SUPPLIER_EMPTY")
    private Integer supplierId;
    // Nhân viên phụ trách
    @NotBlank(message = "EMPLOYEE_EMPTY")
    @NotNull(message = "EMPLOYEE_EMPTY")
    private String employeeCode;
    // Ngày giao hàng
    @NotNull(message = "DELIVERY_DATE")
    private LocalDate deliveryDate;
    // Danh sách sản phẩm
    @NotNull(message = "PURCHASE_ORDER_EMPTY")
    private List<ProductPurchaseOrderDTO> products;
}
