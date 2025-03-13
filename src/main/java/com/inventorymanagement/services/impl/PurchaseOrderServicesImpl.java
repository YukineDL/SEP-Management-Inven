package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.constant.PURCHASE_ORDER_APPROVE;
import com.inventorymanagement.dto.*;
import com.inventorymanagement.dto.response.ProductPurchaseOrderDTO;
import com.inventorymanagement.entity.*;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.*;
import com.inventorymanagement.repository.custom.ProductPurchaseCustomRepository;
import com.inventorymanagement.repository.custom.PurchaseOrderCustomRepository;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.services.IPurchaseOrderServices;
import com.inventorymanagement.services.ISupplierServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderServicesImpl implements IPurchaseOrderServices {
    private final IEmployeeServices employeeServices;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProcessCheckRepository processCheckRepository;
    private final ProductPurchaseOrderRepository productPurchaseOrderRepository;
    private final ProductPurchaseCustomRepository productPurchaseCustomRepository;
    private final PurchaseOrderCustomRepository purchaseOrderCustomRepository;
    private final ISupplierServices supplierServices;

    @Override
    public void createPurchaseOrder(PurchaseOrderCreateDTO dto) throws InventoryException {
        Supplier supplier = supplierServices.findById(dto.getSupplierId());
        EmployeeDTO employee = employeeServices.findByCode(dto.getEmployeeCode());
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .code(createPurchaseOrderCode())
                .approve(PURCHASE_ORDER_APPROVE.WAITING.name())
                .deliveryStatus(Constants.WAITING_DELIVERY)
                .employeeCode(employee.getCode())
                .supplierId(supplier.getId())
                .deliveryDate(dto.getDeliveryDate())
                .createAt(LocalDate.now())
                .build();
        purchaseOrderRepository.save(purchaseOrder);
        // start sync
        String key = UUID.randomUUID().toString();
        ProcessCheck process = ProcessCheck.builder()
                .status(Constants.PROCESSING)
                .checkSync(key)
                .createAt(LocalDateTime.now())
                .build();
        processCheckRepository.save(process);
        CompletableFuture.runAsync(
                () -> {
                    var processCheck = processCheckRepository.findByCheckSync(key).get();
                    try {
                        List<ProductPurchaseOrder> allProducts = new ArrayList<>();
                        for(ProductPurchaseOrderDTO item : dto.getProducts()) {
                            ProductPurchaseOrder product = ProductPurchaseOrder.builder()
                                    .productCode(item.getProductCode())
                                    .purchaseOrderCode(purchaseOrder.getCode())
                                    .quantity(item.getQuantity())
                                    .build();
                            allProducts.add(product);
                        }
                        productPurchaseOrderRepository.saveAll(allProducts);
                        processCheck.setStatus(Constants.SUCCESS);
                        processCheckRepository.save(processCheck);
                    } catch (Exception e){
                        log.info(e.getMessage());
                        processCheck.setStatus(Constants.FAIL);
                        processCheckRepository.save(processCheck);
                    }
                }
        );
    }

    @Override
    public void approvePurchaseOrder(String authHeader, String purchaseCode, String approveStatus) throws InventoryException {
        checkPermission(authHeader);
        Optional<PurchaseOrder> purchaseOrderOp = purchaseOrderRepository.findByCode(purchaseCode);
        if(purchaseOrderOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.PURCHASE_ORDER_NOT_EXIST,
                    ExceptionMessage.messages.get(ExceptionMessage.PURCHASE_ORDER_NOT_EXIST)
            );
        }
        PurchaseOrder purchaseOrder = purchaseOrderOp.get();
        purchaseOrder.setApprove(approveStatus);
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    public void receivePurchaseOrder(String authHeader, String purchaseCode) throws InventoryException {
        checkPermission(authHeader);
        Optional<PurchaseOrder> purchaseOrderOp = purchaseOrderRepository.findByCode(purchaseCode);
        if(purchaseOrderOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.PURCHASE_ORDER_NOT_EXIST,
                    ExceptionMessage.messages.get(ExceptionMessage.PURCHASE_ORDER_NOT_EXIST)
            );
        }
        PurchaseOrder purchaseOrder = purchaseOrderOp.get();
        if(StringUtils.equals(PURCHASE_ORDER_APPROVE.WAITING.name()
                ,purchaseOrder.getApprove()) || StringUtils.equals(
                        PURCHASE_ORDER_APPROVE.REJECTED.name()
                ,purchaseOrder.getApprove())){
            throw new InventoryException(
                    ExceptionMessage.PURCHASE_ORDER_STATUS_INCORRECT,
                    ExceptionMessage.messages.get(ExceptionMessage.PURCHASE_ORDER_STATUS_INCORRECT)
            );
        }
        purchaseOrder.setDeliveryStatus(Constants.RECEIVE_DELIVERY);
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    public PurchaseDTO getPurchaseOrder(String purchaseCode) throws InventoryException {
        Optional<PurchaseOrder> purchaseOrderOp = purchaseOrderRepository.findByCode(purchaseCode);
        if(purchaseOrderOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.PURCHASE_ORDER_NOT_EXIST,
                    ExceptionMessage.messages.get(ExceptionMessage.PURCHASE_ORDER_NOT_EXIST)
            );
        }
        PurchaseOrder purchaseOrder = purchaseOrderOp.get();
        List<ProductPurchaseDTO> products = productPurchaseCustomRepository.findByPurchaseOrderCode(purchaseOrder.getCode());
        Supplier supplier = supplierServices.findById(purchaseOrder.getSupplierId());
        EmployeeDTO employeeDTO = employeeServices.findByCode(purchaseOrder.getEmployeeCode());
        return PurchaseDTO.builder()
                .code(purchaseCode)
                .employee(employeeDTO)
                .products(products)
                .supplier(supplier)
                .approve(purchaseOrder.getApprove())
                .deliveryStatus(purchaseOrder.getDeliveryStatus())
                .deliveryDate(purchaseOrder.getDeliveryDate())
                .build();
    }

    @Override
    public Page<PurchaseOrder> findBySearchRequest(PurchaseOrderReqDTO reqDTO) {
        return purchaseOrderCustomRepository.findBySearchRequest(reqDTO);
    }
    @Transactional
    @Override
    public String updatePurchaseOrder(PurchaseOrderCreateDTO createDTO, String purchaseOrderCode) throws InventoryException {
        Optional<PurchaseOrder> purchaseOrderOP = purchaseOrderRepository.findByCode(purchaseOrderCode);
        if(purchaseOrderOP.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.PURCHASE_ORDER_NOT_EXIST,
                    ExceptionMessage.messages.get(ExceptionMessage.PURCHASE_ORDER_NOT_EXIST)
            );
        }
        PurchaseOrder purchaseOrder = purchaseOrderOP.get();
        if(StringUtils.equals(purchaseOrder.getApprove(),PURCHASE_ORDER_APPROVE.APPROVED.name())){
            throw new InventoryException(
                    ExceptionMessage.PURCHASE_ORDER_APPROVED,
                    ExceptionMessage.messages.get(ExceptionMessage.PURCHASE_ORDER_APPROVED));
        }
        EmployeeDTO employee = employeeServices.findByCode(createDTO.getEmployeeCode());
        Supplier supplier = supplierServices.findById(createDTO.getSupplierId());
        purchaseOrder.setEmployeeCode(employee.getCode());
        purchaseOrder.setSupplierId(supplier.getId());

        // clear all data product purchase before adding new data
        productPurchaseOrderRepository.deleteByPurchaseOrderCode(purchaseOrder.getCode());
        String key = UUID.randomUUID().toString();
        ProcessCheck process = ProcessCheck.builder()
                .checkSync(key)
                .status(Constants.PROCESSING)
                .createAt(LocalDateTime.now())
                .build();
        processCheckRepository.save(process);
        CompletableFuture.runAsync(() -> {
            try {
                List<ProductPurchaseOrder> allProducts = new ArrayList<>();
                for(ProductPurchaseOrderDTO item : createDTO.getProducts()) {
                    ProductPurchaseOrder product = ProductPurchaseOrder.builder()
                            .productCode(item.getProductCode())
                            .purchaseOrderCode(purchaseOrder.getCode())
                            .quantity(item.getQuantity())
                            .build();
                    allProducts.add(product);
                }
                productPurchaseOrderRepository.saveAll(allProducts);
                process.setStatus(Constants.SUCCESS);
                processCheckRepository.save(process);
            } catch (Exception e){
                log.info(e.getMessage());
                process.setStatus(Constants.FAIL);
                processCheckRepository.save(process);
            }
        });
        return key;
    }

    private void checkPermission(String authHeader) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(!Constants.LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
    }
    private String createPurchaseOrderCode(){
        return Constants.PURCHASE_ORDER_CODE +
                String.format("%05d", purchaseOrderRepository.count() + 1);
    }

}
