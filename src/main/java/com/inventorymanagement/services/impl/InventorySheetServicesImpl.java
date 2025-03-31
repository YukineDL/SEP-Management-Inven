package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.constant.RoleEnum;
import com.inventorymanagement.dto.ProductSheetDTO;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.entity.InventorySheet;
import com.inventorymanagement.entity.ProcessCheck;
import com.inventorymanagement.entity.ProductSheet;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.InventorySheetRepository;
import com.inventorymanagement.repository.ProcessCheckRepository;
import com.inventorymanagement.repository.ProductSheetRepository;
import com.inventorymanagement.repository.custom.IProductSheetCustomRepository;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.services.IInventorySheetServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class InventorySheetServicesImpl implements IInventorySheetServices {
    private final IProductSheetCustomRepository productSheetCustomRepository;
    private final IEmployeeServices employeeService;
    private final InventorySheetRepository inventorySheetRepository;
    private final ProcessCheckRepository processCheckRepository;
    private final ProductSheetRepository productSheetRepository;
    @Override
    public void createInventorySheet(String authHeader, LocalDate startDate, LocalDate endDate) throws InventoryException {
        Employee me = employeeService.getFullInformation(authHeader);
        if(me == null || me.getRoleCode().equals(RoleEnum.SALE.name())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        InventorySheet inventorySheet = InventorySheet.builder()
                .code(this.createCode())
                .startDate(startDate)
                .endDate(endDate)
                .createAt(LocalDate.now())
                .employeeCode(me.getCode())
                .build();
        inventorySheetRepository.save(inventorySheet);
        String key = UUID.randomUUID().toString();
        ProcessCheck processCheck = ProcessCheck.builder()
                .createAt(LocalDateTime.now())
                .status(Constants.PROCESSING)
                .checkSync(key)
                .build();
        processCheckRepository.save(processCheck);
        CompletableFuture.runAsync(() -> {
            try {
                List<ProductSheetDTO> dataSum = productSheetCustomRepository.getSumDataFromDateAndToDate(startDate, endDate);
                List<ProductSheet> results = new ArrayList<>();
                for(ProductSheetDTO dto : dataSum){
                    ProductSheet productSheet = new ProductSheet(dto,inventorySheet.getCode());
                    results.add(productSheet);
                }
                productSheetRepository.saveAll(results);
                processCheck.setStatus(Constants.SUCCESS);
                processCheckRepository.save(processCheck);
            } catch (Exception e){
                processCheck.setStatus(Constants.FAIL);
                processCheckRepository.save(processCheck);
            }
        });
    }
    private String createCode(){
        return Constants.INVENTORY_SHEET_CODE +
               String.format("%05d", inventorySheetRepository.count() + 1);
    }
}
