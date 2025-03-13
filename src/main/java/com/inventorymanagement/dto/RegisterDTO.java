package com.inventorymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {
    private String username;
    private String password;
    private String roleCode;
    private String phoneNumber;
    private String name;
    private String inventoryCode;
}
