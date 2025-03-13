package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.constant.PURCHASE_ORDER_APPROVE;
import com.inventorymanagement.dto.InventoryReceiptReqDTO;
import com.inventorymanagement.dto.response.BatchNumberDTO;
import com.inventorymanagement.entity.BatchNumber;
import com.inventorymanagement.entity.InventoryReceipt;
import com.inventorymanagement.entity.ProcessCheck;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.BatchNumberRepository;
import com.inventorymanagement.repository.InventoryReceiptRepository;
import com.inventorymanagement.repository.ProcessCheckRepository;
import com.inventorymanagement.services.IInventoryReceiptServices;
import com.inventorymanagement.services.IPurchaseOrderServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReceiptServicesImpl implements IInventoryReceiptServices {
    private final InventoryReceiptRepository inventoryReceiptRepository;
    private final ProcessCheckRepository processCheckRepository;
    private final BatchNumberRepository batchNumberRepository;
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
        InventoryReceipt inventoryReceipt = InventoryReceipt.builder()
                .code(createCode())
                .createAt(LocalDate.now())
                .numberOfReceipts(receiptReqDTO.getNumberOfReceipts())
                .accountingDate(receiptReqDTO.getAccountingDate() != null ? receiptReqDTO.getAccountingDate() : LocalDate.now())
                .documentDate(receiptReqDTO.getDocumentDate() != null ? receiptReqDTO.getDocumentDate() : LocalDate.now())
                .purchaseOrderCode(receiptReqDTO.getPurchaseOrderCode())
                .statusImport(Constants.STATUS_IMPORT_WAITING)
                .approve(PURCHASE_ORDER_APPROVE.WAITING.name())
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
                List<BatchNumber> batchNumbers = new ArrayList<>();
                for (BatchNumberDTO item : receiptReqDTO.getBatchNumbers()) {
                    BatchNumber batchNumber = new BatchNumber(item, inventoryReceipt.getCode());
                    batchNumbers.add(batchNumber);
                }
                batchNumberRepository.saveAll(batchNumbers);
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
    private String createCode(){
        return Constants.INVENTORY_RECEIPT_CODE +
                String.format("%05d", inventoryReceiptRepository.count() + 1);
    }
}
