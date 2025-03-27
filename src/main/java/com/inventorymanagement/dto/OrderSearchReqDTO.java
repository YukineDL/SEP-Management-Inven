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
public class OrderSearchReqDTO {
    private String code;
    private String deliveryStatus;
    private String approveStatus;
    private String employeeCode;
    private String customerPhoneNumber;
    private Double totalAmountFrom;
    private Double totalAmountTo;
    private LocalDate fromDate;
    private LocalDate toDate;
}
