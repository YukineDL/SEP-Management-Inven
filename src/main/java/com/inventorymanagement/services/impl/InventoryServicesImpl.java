package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.RoleEnum;
import com.inventorymanagement.dto.InventoryDTO;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.entity.Inventory;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.EmployeeRepository;
import com.inventorymanagement.repository.InventoryRepository;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.services.IInventoryServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServicesImpl implements IInventoryServices {
    private final InventoryRepository inventoryRepository;
    private final IEmployeeServices employeeServices;
    private final EmployeeRepository employeeRepository;
    private final List<String> LIST_MANAGER = new ArrayList<>(List.of(RoleEnum.ADMIN.name(),
            RoleEnum.MANAGER.name()));

    @Override
    public void createInventory(InventoryDTO inventoryDTO, String authHeader) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        List<Inventory> inventories = inventoryRepository.findAll();
        if(!LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        Inventory inventory = Inventory.builder()
                .code(createCodeWithValue(inventories.isEmpty() ? 1 : inventories.size() + 1))
                .name(inventoryDTO.getName())
                .address(inventoryDTO.getAddress())
                .build();
        inventoryRepository.save(inventory);
        me.setInventoryCode(inventory.getCode());
        employeeRepository.save(me);
    }
    private String createCodeWithValue(int value){
        return "I" + value;
    }
}
