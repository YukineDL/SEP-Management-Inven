package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.constant.PURCHASE_ORDER_APPROVE;
import com.inventorymanagement.dto.*;
import com.inventorymanagement.entity.ProcessCheck;
import com.inventorymanagement.entity.Product;
import com.inventorymanagement.entity.ReturnForm;
import com.inventorymanagement.entity.ReturnProduct;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.ProcessCheckRepository;
import com.inventorymanagement.repository.ProductRepository;
import com.inventorymanagement.repository.ReturnFormRepository;
import com.inventorymanagement.repository.ReturnProductRepository;
import com.inventorymanagement.repository.custom.ReturnFormCustomRepository;
import com.inventorymanagement.services.IOrderServices;
import com.inventorymanagement.services.IReturnFormServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnFormServicesImpl implements IReturnFormServices {
    private final ReturnFormRepository returnFormRepository;
    private final ProcessCheckRepository processCheckRepository;
    private final ReturnProductRepository returnProductRepository;
    private final ReturnFormCustomRepository returnFormCustomRepository;
    private final ProductRepository productRepository;
    private final IOrderServices orderServices;
    @Override
    public void createReturnForm(ReturnFormCreateDTO dto) throws InventoryException {
        OrderDTO orderDTO = orderServices.findOrderByCode(dto.getOrderCode());
        Map<String, Integer> returnProductMap = new HashMap<>();
        for (ReturnProductCreateDTO item : dto.getProducts()) {
            returnProductMap.merge(item.getProductCode(), item.getQuantity(), Integer::sum);
        }
        for(ProductOrderDTO item : orderDTO.getOrderProducts()){
            Integer quantityReturn = returnProductMap.get(item.getCode());
            if(Objects.isNull(quantityReturn)){
                throw new InventoryException(
                        ExceptionMessage.RETURN_PRODUCT_NOT_IN_ORDER,
                        ExceptionMessage.messages.get(ExceptionMessage.RETURN_PRODUCT_NOT_IN_ORDER)
                );
            }
            if(quantityReturn > item.getQuantity()){
                throw new InventoryException(
                    ExceptionMessage.RETURN_PRODUCT_OVER_ORDER,
                        String.format(ExceptionMessage.messages.get(ExceptionMessage.RETURN_PRODUCT_OVER_ORDER), item.getCode())
                );
            }
        }
        if(!orderDTO.getApproveStatus().equals(PURCHASE_ORDER_APPROVE.APPROVED.name())){
            throw new InventoryException(
                    ExceptionMessage.ORDER_APPROVE_STATUS_INVALID,
                    ExceptionMessage.ORDER_APPROVE_STATUS_INVALID
            );
        }
        String code = createCode();
        ReturnForm returnForm = ReturnForm.builder()
                .code(code)
                .createAt(LocalDateTime.now())
                .orderCode(dto.getOrderCode())
                .isUsed(false)
                .approveStatus(PURCHASE_ORDER_APPROVE.WAITING.name())
                .build();
        returnFormRepository.save(returnForm);
        String key = UUID.randomUUID().toString();
        ProcessCheck processCheck = ProcessCheck.builder()
                .createAt(LocalDateTime.now())
                .status(Constants.PROCESSING)
                .checkSync(key)
                .build();
        processCheckRepository.save(processCheck);
        CompletableFuture.runAsync(() -> {
            try {
                List<ReturnProduct> products = new ArrayList<>();
                for (ReturnProductCreateDTO item : dto.getProducts()) {
                    ReturnProduct returnProduct = ReturnProduct.builder()
                            .returnFormCode(code)
                            .quantityReturn(item.getQuantity())
                            .reason(item.getReason())
                            .statusProduct(item.getStatusProduct())
                            .productCode(item.getProductCode())
                            .dateExpired(item.getDateExpired())
                            .dateOfManufacture(item.getDateOfManufacture())
                            .build();
                    products.add(returnProduct);
                }
                returnProductRepository.saveAll(products);
                processCheck.setStatus(Constants.SUCCESS);
                processCheckRepository.save(processCheck);
            } catch (Exception e){
                processCheck.setStatus(Constants.FAIL);
                processCheckRepository.save(processCheck);
            }
        });
    }

    @Override
    public void approveReturnForm(String returnCode) throws InventoryException {
        Optional<ReturnForm> returnFormOp = returnFormRepository.findByCode(returnCode);
        if (returnFormOp.isEmpty()) {
            throw new InventoryException(
                    ExceptionMessage.RETURN_FORM_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.RETURN_FORM_NOT_EXISTED)
            );
        }
        ReturnForm returnForm = returnFormOp.get();
        returnForm.setApproveStatus(PURCHASE_ORDER_APPROVE.APPROVED.name());
        returnFormRepository.save(returnForm);
    }

    @Override
    public void rejectReturnForm(String returnCode) throws InventoryException {
        Optional<ReturnForm> returnFormOp = returnFormRepository.findByCode(returnCode);
        if (returnFormOp.isEmpty()) {
            throw new InventoryException(
                    ExceptionMessage.RETURN_FORM_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.RETURN_FORM_NOT_EXISTED)
            );
        }
        ReturnForm returnForm = returnFormOp.get();
        returnForm.setApproveStatus(PURCHASE_ORDER_APPROVE.REJECTED.name());
        returnFormRepository.save(returnForm);
    }

    @Override
    public Page<ReturnForm> findBySearchRequest(ReturnFormSearchReq dto, Pageable pageable) {
        return returnFormCustomRepository.findBySearchReq(dto, pageable);
    }

    @Override
    public ReturnFormDTO findReturnForm(String returnCode) throws InventoryException {
        Optional<ReturnForm> returnFormOp = returnFormRepository.findByCode(returnCode);
        if(returnFormOp.isEmpty()) {
            throw new InventoryException(
                    ExceptionMessage.RETURN_FORM_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.RETURN_FORM_NOT_EXISTED)
            );
        }
        var returnProducts = returnProductRepository.findByReturnFormCode(returnCode);
        var productsMap = productRepository.findAll().stream().collect(Collectors.toMap(
                Product::getCode,product -> product
        ));
        List<ReturnProductDTO> results = new ArrayList<>();
        for (ReturnProduct returnProduct : returnProducts) {
            ReturnProductDTO item = ReturnProductDTO.builder()
                    .id(returnProduct.getId())
                    .productCode(returnProduct.getProductCode())
                    .quantityReturn(returnProduct.getQuantityReturn())
                    .reason(returnProduct.getReason())
                    .productInformation(productsMap.get(returnProduct.getProductCode()))
                    .build();
            results.add(item);
        }
        return ReturnFormDTO.builder()
                .returnProducts(results)
                .returnForm(returnFormOp.get())
                .build();
    }

    private String createCode(){
        return Constants.RETURN_FROM_CODE +
               String.format("%05d", returnFormRepository.count() + 1);
    }
}
