package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.constant.PRODUCT_STATUS;
import com.inventorymanagement.constant.PURCHASE_ORDER_APPROVE;
import com.inventorymanagement.dto.InventoryReceiptDTO;
import com.inventorymanagement.dto.InventoryReceiptReqDTO;
import com.inventorymanagement.dto.InventoryReceiptResDTO;
import com.inventorymanagement.dto.InventoryReceiptSearchReq;
import com.inventorymanagement.dto.response.BatchNumberDTO;
import com.inventorymanagement.entity.*;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.*;
import com.inventorymanagement.repository.custom.BatchNumberTempCustomRepository;
import com.inventorymanagement.repository.custom.IBatchNumberCustomRepository;
import com.inventorymanagement.repository.custom.InventoryReceiptCustomRepository;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.services.IInventoryReceiptServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReceiptServicesImpl implements IInventoryReceiptServices {
    private final IEmployeeServices employeeServices;
    private final InventoryReceiptRepository inventoryReceiptRepository;
    private final ProcessCheckRepository processCheckRepository;
    private final BatchNumberTempRepository batchNumberTempRepository;
    private final BatchNumberRepository batchNumberRepository;
    private final InventoryReceiptCustomRepository inventoryReceiptCustomRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final IBatchNumberCustomRepository batchNumberCustomRepository;
    private final BatchNumberTempCustomRepository batchNumberTempCustomRepository;
    private final EmployeeRepository employeeRepository;
    @Override
    public String createReceipt(InventoryReceiptReqDTO receiptReqDTO) throws InventoryException {
        List<String> statusApprove = List.of(PURCHASE_ORDER_APPROVE.APPROVED.name(),
                PURCHASE_ORDER_APPROVE.WAITING.name());
        if(Boolean.TRUE.equals(inventoryReceiptRepository.existsByPurchaseOrderCodeAndApproveIn(receiptReqDTO.getPurchaseOrderCode()
                , statusApprove))){
            throw new InventoryException(
                    ExceptionMessage.INVALID_CREATE_INVENTORY_RECEIPT,
                    ExceptionMessage.messages.get(ExceptionMessage.INVALID_CREATE_INVENTORY_RECEIPT)
            );
        }
        Optional<PurchaseOrder> purchaseOrderOP = purchaseOrderRepository.findByCode(receiptReqDTO.getPurchaseOrderCode());
        if(purchaseOrderOP.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.PURCHASE_ORDER_NOT_EXIST,
                    ExceptionMessage.messages.get(ExceptionMessage.PURCHASE_ORDER_NOT_EXIST)
            );
        }
        PurchaseOrder purchaseOrder = purchaseOrderOP.get();
        purchaseOrder.setIsUsed(true);
        purchaseOrderRepository.save(purchaseOrder);
        InventoryReceipt inventoryReceipt = InventoryReceipt.builder()
                .code(createCode())
                .createAt(LocalDate.now())
                .numberOfReceipts(receiptReqDTO.getNumberOfReceipts())
                .accountingDate(receiptReqDTO.getAccountingDate() != null ? receiptReqDTO.getAccountingDate() : LocalDate.now())
                .documentDate(receiptReqDTO.getDocumentDate() != null ? receiptReqDTO.getDocumentDate() : LocalDate.now())
                .purchaseOrderCode(receiptReqDTO.getPurchaseOrderCode())
                .employeeCode(receiptReqDTO.getEmployeeCode())
                .statusImport(Constants.STATUS_IMPORT_WAITING)
                .approve(PURCHASE_ORDER_APPROVE.WAITING.name())
                .totalAmount(receiptReqDTO.getTotalAmount())
                .createAtDateTime(LocalDateTime.now())
                .totalQuantity(purchaseOrder.getTotalQuantity())
                .supplierId(purchaseOrder.getSupplierId())
                .build();
        inventoryReceiptRepository.save(inventoryReceipt);

        // start process
        String key = UUID.randomUUID().toString();
        ProcessCheck process = ProcessCheck.builder()
                .createAt(LocalDateTime.now())
                .status(Constants.PROCESSING)
                .checkSync(key)
                .build();
        processCheckRepository.save(process);
        CompletableFuture.runAsync(() -> {
            try {
                List<BatchNumberTemp> batchNumberTemps = new ArrayList<>();
                for (BatchNumberDTO item : receiptReqDTO.getBatchNumbers()) {
                    BatchNumberTemp batchNumberTemp = new BatchNumberTemp(item, inventoryReceipt.getCode());
                    batchNumberTemp.setStatus(PRODUCT_STATUS.NEW.name());
                    batchNumberTemps.add(batchNumberTemp);
                }
                batchNumberTempRepository.saveAll(batchNumberTemps);
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

    @Override
    public void approveInventoryReceipt(String authHeader, String inventoryReceiptCode) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(!Constants.LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        Optional<InventoryReceipt> inventoryReceiptOptional = inventoryReceiptRepository.findByCode(inventoryReceiptCode);
        if(inventoryReceiptOptional.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.INVENTORY_RECEIPT_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.INVENTORY_RECEIPT_NOT_EXISTED)
            );
        }
        InventoryReceipt inventoryReceipt = inventoryReceiptOptional.get();
        inventoryReceipt.setApprove(PURCHASE_ORDER_APPROVE.APPROVED.name());
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByCode(inventoryReceipt.getPurchaseOrderCode()).get();
        List<BatchNumberTemp> batchNumberTemps = batchNumberTempRepository.findByInventoryReceiptCode(inventoryReceipt.getCode());
        List<BatchNumber> batchNumbers = new ArrayList<>();
        for (BatchNumberTemp item : batchNumberTemps){
            BatchNumber batchNumber = new BatchNumber(item,inventoryReceipt.getCode());
            batchNumbers.add(batchNumber);
        }
        purchaseOrder.setDeliveryStatus(Constants.STATUS_IMPORT_SUCCESS);
        inventoryReceipt.setUsername(me.getName());
        inventoryReceipt.setActionTime(LocalDateTime.now());
        batchNumberRepository.saveAll(batchNumbers);
        inventoryReceiptRepository.save(inventoryReceipt);
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    @Override
    public void rejectInventoryReceipt(String authHeader, String inventoryReceiptCode) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(!Constants.LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        Optional<InventoryReceipt> inventoryReceiptOptional = inventoryReceiptRepository.findByCode(inventoryReceiptCode);
        if(inventoryReceiptOptional.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.INVENTORY_RECEIPT_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.INVENTORY_RECEIPT_NOT_EXISTED)
            );
        }

        InventoryReceipt inventoryReceipt = inventoryReceiptOptional.get();
        if(inventoryReceipt.getApprove().equals(PURCHASE_ORDER_APPROVE.APPROVED.name())){
            throw new InventoryException(
                    ExceptionMessage.INVENTORY_RECEIPT_APPROVE,
                    ExceptionMessage.messages.get(ExceptionMessage.INVENTORY_RECEIPT_APPROVE)
            );
        }

        Optional<PurchaseOrder> purchaseOrderOP = purchaseOrderRepository.findByCode(inventoryReceipt.getPurchaseOrderCode());
        if(purchaseOrderOP.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.PURCHASE_ORDER_NOT_EXIST,
                    ExceptionMessage.messages.get(ExceptionMessage.PURCHASE_ORDER_NOT_EXIST)
            );
        }

        PurchaseOrder purchaseOrder = purchaseOrderOP.get();
        purchaseOrder.setIsUsed(true);
        purchaseOrder.setApprove(PURCHASE_ORDER_APPROVE.REJECTED.name());
        purchaseOrderRepository.save(purchaseOrder);

        batchNumberTempRepository.deleteByInventoryReceiptCode(inventoryReceiptCode);
        inventoryReceipt.setApprove(PURCHASE_ORDER_APPROVE.REJECTED.name());
        inventoryReceipt.setUsername(me.getName());
        inventoryReceipt.setActionTime(LocalDateTime.now());
        inventoryReceiptRepository.save(inventoryReceipt);
    }

    @Override
    public Page<InventoryReceiptResDTO> findBySearchRequest(InventoryReceiptSearchReq req, Pageable pageable){
        Page<InventoryReceipt> pageContent = inventoryReceiptCustomRepository.findBySearchRequest(req,pageable);
        Map<Integer, Supplier> mapSupplier = supplierRepository.findAll().stream().collect(Collectors.toMap(
            Supplier::getId, supplier -> supplier
        ));
        List<InventoryReceiptResDTO> results = new ArrayList<>();
        for(InventoryReceipt item : pageContent.getContent()){
            InventoryReceiptResDTO dto = new InventoryReceiptResDTO();
            dto.setInventoryReceipt(item);
            dto.setSupplier(mapSupplier.get(item.getSupplierId()));
            results.add(dto);
        }
        return new PageImpl<>(results, pageable,pageContent.getTotalElements());
    }

    @Override
    public InventoryReceiptDTO findByCode(String inventoryCode) throws InventoryException {
        Optional<InventoryReceipt> inventoryReceiptOptional = inventoryReceiptRepository.findByCode(inventoryCode);
        if(inventoryReceiptOptional.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.INVENTORY_RECEIPT_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.INVENTORY_RECEIPT_NOT_EXISTED)
            );
        }
        Map<Integer, Supplier> mapSupplier = supplierRepository.findAll().stream().collect(Collectors.toMap(
                Supplier::getId, supplier -> supplier
        ));
        Map<String, Employee> employeeMap = employeeRepository.findAll().stream().collect(Collectors.toMap(
                Employee::getCode, employee -> employee
        ));
        InventoryReceiptDTO dto = new InventoryReceiptDTO(inventoryReceiptOptional.get());
        dto.setItems(batchNumberTempCustomRepository.findBatchNumberTempByCode(inventoryCode));
        dto.setItemsInInventory(batchNumberCustomRepository.findAllByInventoryReceiptCode(inventoryCode));
        dto.setSupplier(mapSupplier.get(inventoryReceiptOptional.get().getSupplierId()));
        dto.setEmployee(employeeMap.get(inventoryReceiptOptional.get().getEmployeeCode()));
        return dto;
    }
    @Transactional
    @Override
    public void updateInventoryReceipt(String inventoryReceiptCode, InventoryReceiptReqDTO dto) throws InventoryException {
        Optional<InventoryReceipt> inventoryReceiptOptional = inventoryReceiptRepository.findByCode(inventoryReceiptCode);
        if(inventoryReceiptOptional.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.INVENTORY_RECEIPT_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.INVENTORY_RECEIPT_NOT_EXISTED)
            );
        }
        InventoryReceipt inventoryReceipt = inventoryReceiptOptional.get();
        inventoryReceipt.setNumberOfReceipts(dto.getNumberOfReceipts());
        inventoryReceipt.setAccountingDate(dto.getAccountingDate());
        inventoryReceipt.setDocumentDate(dto.getDocumentDate());
        // delete item in old list
        batchNumberTempRepository.deleteByInventoryReceiptCode(inventoryReceiptCode);
        List<BatchNumberTemp> batchNumberTemps = new ArrayList<>();
        int totalQuantity = 0;
        for (BatchNumberDTO item : dto.getBatchNumbers()) {
            BatchNumberTemp batchNumberTemp = new BatchNumberTemp(item, inventoryReceipt.getCode());
            batchNumberTemps.add(batchNumberTemp);
            totalQuantity += item.getQuantityShipped();
        }
        inventoryReceipt.setTotalAmount(dto.getTotalAmount());
        inventoryReceipt.setTotalQuantity(totalQuantity);
        batchNumberTempRepository.saveAll(batchNumberTemps);
    }

    private String createCode(){
        return Constants.INVENTORY_RECEIPT_CODE +
                String.format("%05d", inventoryReceiptRepository.count() + 1);
    }
}
