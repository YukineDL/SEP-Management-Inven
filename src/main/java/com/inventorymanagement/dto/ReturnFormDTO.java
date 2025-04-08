package com.inventorymanagement.dto;

import com.inventorymanagement.entity.Customer;
import com.inventorymanagement.entity.Employee;
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
    private Customer customer;
    private Employee employee;
    private List<ReturnProductDTO> returnProducts;
}
