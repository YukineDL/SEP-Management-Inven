package com.inventorymanagement.dto;

import com.inventorymanagement.entity.InventorySheet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySheetDTO {
    private Page<ProductSheetDTO> data;
    private InventorySheet sheet;
}
