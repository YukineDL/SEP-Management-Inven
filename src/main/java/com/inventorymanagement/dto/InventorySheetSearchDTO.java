package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventorySheetSearchDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isReview;
}
