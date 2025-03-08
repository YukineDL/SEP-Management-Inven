package com.inventorymanagement.dto.response;

import com.inventorymanagement.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenResponseDTO {
    private String token;
    private Employee info;
}
