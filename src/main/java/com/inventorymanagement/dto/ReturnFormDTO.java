package com.inventorymanagement.dto;

import com.inventorymanagement.entity.ReturnForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnFormDTO {
    private ReturnForm returnForm;
    private List<ReturnProductDTO> returnProducts;
}
