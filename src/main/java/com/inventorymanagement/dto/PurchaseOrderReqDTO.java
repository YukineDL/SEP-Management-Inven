package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderReqDTO {
    private String code;
    private String approveStatus;
    private String deliveryStatus;
    private LocalDate createAt;
    private Pageable pageable;
    private Integer supplierId;
    private LocalDate deliveryDate;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Boolean isUsed;
}
