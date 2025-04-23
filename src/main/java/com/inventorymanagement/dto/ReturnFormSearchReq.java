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
public class ReturnFormSearchReq {
    private String code;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Boolean isUsed;
    private Integer customerId;
    private Double amountTo;
    private Double amountFrom;
    private String approveStatus;
}
