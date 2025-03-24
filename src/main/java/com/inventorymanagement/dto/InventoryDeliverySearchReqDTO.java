package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDeliverySearchReqDTO {
    private String code;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer customerId;
    private Double totalAmountTo;
    private Double totalAmountFrom;
    private String approveStatus;
}
