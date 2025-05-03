package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.constant.PURCHASE_ORDER_APPROVE;
import com.inventorymanagement.constant.RoleEnum;
import com.inventorymanagement.dto.*;
import com.inventorymanagement.entity.*;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.*;
import com.inventorymanagement.repository.custom.ReturnFormCustomRepository;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.services.IOrderServices;
import com.inventorymanagement.services.IProductServices;
import com.inventorymanagement.services.IReturnFormServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnFormServicesImpl implements IReturnFormServices {
    private final ReturnFormRepository returnFormRepository;
    private final ProcessCheckRepository processCheckRepository;
    private final ReturnProductRepository returnProductRepository;
    private final ReturnFormCustomRepository returnFormCustomRepository;
    private final ProductRepository productRepository;
    private final IProductServices productServices;
    private final IOrderServices orderServices;
    private final IEmployeeServices employeeServices;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final List<String> LIST_APPROVE = Arrays.asList(RoleEnum.ADMIN.name(),RoleEnum.SALE.name());
    @Override
    public void  createReturnForm(ReturnFormCreateDTO dto, String authHeader) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(me == null || me.getRoleCode().equals(RoleEnum.EMPLOYEE.name())) {
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        OrderDTO orderDTO = orderServices.findOrderByCode(dto.getOrderCode());
        Map<String, Integer> returnProductMap = dto.getProducts().stream()
                .collect(Collectors.toMap(
                        ReturnProductCreateDTO::getProductCode,
                        ReturnProductCreateDTO::getQuantity,
                        Integer::sum
                ));
        var orderProductMap = orderDTO.getOrderProducts().stream().collect(
                Collectors.toMap(ProductOrderDTO::getCode,Function.identity())
        );
        var returnFormExist = returnFormRepository.findByApproveStatusInAndOrderCode(
                List.of(PURCHASE_ORDER_APPROVE.WAITING.name(),
                        PURCHASE_ORDER_APPROVE.APPROVED.name()),
                dto.getOrderCode()
        ).stream().map(ReturnForm::getCode).toList();
        var productReturnExist = returnProductRepository.findByReturnFormCodeIn(returnFormExist).stream().collect(
                Collectors.toMap(
                        ReturnProduct::getProductCode,
                        ReturnProduct::getQuantityReturn,
                        Integer::sum
                )
        );
        for (ReturnProductCreateDTO item : dto.getProducts()){
            if(!orderProductMap.containsKey(item.getProductCode())){
                throw new InventoryException(
                        ExceptionMessage.RETURN_PRODUCT_NOT_IN_ORDER
                );
            }
            var returnQuantity = returnProductMap.get(item.getProductCode());
            if(orderProductMap.get(item.getProductCode()).getQuantity() < returnQuantity){
                throw new InventoryException(
                        ExceptionMessage.RETURN_PRODUCT_OVER_ORDER,
                        ExceptionMessage.messages.get(ExceptionMessage.RETURN_PRODUCT_OVER_ORDER)
                );
            }
        }
        if(!orderDTO.getApproveStatus().equals(PURCHASE_ORDER_APPROVE.APPROVED.name())){
            throw new InventoryException(
                    ExceptionMessage.ORDER_APPROVE_STATUS_INVALID,
                    ExceptionMessage.ORDER_APPROVE_STATUS_INVALID
            );
        }
        var mapProductDiscount = orderDTO.getOrderProducts().stream().collect(
                Collectors.toMap(ProductOrderDTO::getCode, ProductOrderDTO::getDiscount)
        );
        var productMap = productRepository.findAll().stream().collect(
                Collectors.toMap(Product::getCode, Function.identity())
        );
        String code = createCode();
        ReturnForm returnForm = ReturnForm.builder()
                .code(code)
                .createAt(LocalDateTime.now())
                .orderCode(dto.getOrderCode())
                .customerId(dto.getCustomerId())
                .createBy(me.getCode())
                .isUsed(false)
                .isExport(false)
                .approveStatus(PURCHASE_ORDER_APPROVE.WAITING.name())
                .build();
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
                double totalAmount = 0;
                for (ReturnProductCreateDTO item : dto.getProducts()) {
                    Product product = productMap.get(item.getProductCode());
                    var discount = mapProductDiscount.get(item.getProductCode());
                    ReturnProduct returnProduct = ReturnProduct.builder()
                            .returnFormCode(code)
                            .quantityReturn(item.getQuantity())
                            .reason(item.getReason())
                            .statusProduct(item.getStatusProduct())
                            .productCode(item.getProductCode())
                            .dateExpired(item.getDateExpired())
                            .discount(discount)
                            .dateOfManufacture(item.getDateOfManufacture())
                            .build();
                    totalAmount += item.getQuantity() * product.getSellingPrice() - (item.getQuantity() * product.getSellingPrice() * discount);
                    products.add(returnProduct);
                }
                returnForm.setTotalAmount(Math.ceil(totalAmount));
                returnFormRepository.save(returnForm);
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
    public void approveReturnForm(String authHeader,String returnCode) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(me == null || !LIST_APPROVE.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        Optional<ReturnForm> returnFormOp = returnFormRepository.findByCode(returnCode);
        if (returnFormOp.isEmpty()) {
            throw new InventoryException(
                    ExceptionMessage.RETURN_FORM_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.RETURN_FORM_NOT_EXISTED)
            );
        }
        ReturnForm returnForm = returnFormOp.get();
        returnForm.setApproveStatus(PURCHASE_ORDER_APPROVE.APPROVED.name());
        returnForm.setApproveBy(me.getUsername());
        returnForm.setApproveDate(LocalDateTime.now());
        returnFormRepository.save(returnForm);
    }

    @Override
    public void rejectReturnForm(String authHeader,String returnCode) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(me == null || !LIST_APPROVE.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        Optional<ReturnForm> returnFormOp = returnFormRepository.findByCode(returnCode);
        if (returnFormOp.isEmpty()) {
            throw new InventoryException(
                    ExceptionMessage.RETURN_FORM_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.RETURN_FORM_NOT_EXISTED)
            );
        }
        ReturnForm returnForm = returnFormOp.get();
        returnForm.setApproveStatus(PURCHASE_ORDER_APPROVE.REJECTED.name());
        returnForm.setApproveBy(me.getUsername());
        returnForm.setApproveDate(LocalDateTime.now());
        returnFormRepository.save(returnForm);
    }

    @Override
    public Page<ReturnFormDTO> findBySearchRequest(ReturnFormSearchReq dto, Pageable pageable, String authHeader) throws InventoryException {
         var content = returnFormCustomRepository.findBySearchReq(dto, pageable);
         var employees = employeeRepository.findAll().stream().collect(
                 Collectors.toMap(Employee::getCode, Function.identity())
         );
         var customers = customerRepository.findAll().stream().collect(
                 Collectors.toMap(
                         Customer::getId,Function.identity()
                 )
         );
         var results = new ArrayList<ReturnFormDTO>();
         for (ReturnForm returnForm : content) {
            ReturnFormDTO returnFormDTO = new ReturnFormDTO();
            returnFormDTO.setReturnForm(returnForm);
            returnFormDTO.setEmployee(employees.get(returnForm.getCreateBy()));
            returnFormDTO.setCustomer(customers.get(returnForm.getCustomerId()));
            results.add(returnFormDTO);
         }
        return new PageImpl<>(results, content.getPageable(), content.getTotalPages());
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
        var productsMap = productServices.findAllBySearchRequest(null,PageRequest.of(0, Integer.MAX_VALUE)).stream().collect(Collectors.toMap(
                ProductDTO::getCode,Function.identity()
        ));
        var employees = employeeRepository.findAll().stream().collect(
                Collectors.toMap(Employee::getCode, Function.identity())
        );
        var customers = customerRepository.findAll().stream().collect(
                Collectors.toMap(Customer::getId, Function.identity())
        );
        List<ReturnProductDTO> results = new ArrayList<>();
        for (ReturnProduct returnProduct : returnProducts) {
            ReturnProductDTO item = ReturnProductDTO.builder()
                    .id(returnProduct.getId())
                    .productCode(returnProduct.getProductCode())
                    .quantityReturn(returnProduct.getQuantityReturn())
                    .reason(returnProduct.getReason())
                    .discount(returnProduct.getDiscount())
                    .statusProduct(returnProduct.getStatusProduct())
                    .productInformation(productsMap.get(returnProduct.getProductCode()))
                    .build();
            results.add(item);
        }
        var returnForm = returnFormOp.get();
        return ReturnFormDTO.builder()
                .returnProducts(results)
                .returnForm(returnForm)
                .employee(employees.get(returnForm.getCreateBy()))
                .customer(customers.get(returnForm.getCustomerId()))
                .build();
    }

    private String createCode(){
        return Constants.RETURN_FROM_CODE +
               String.format("%05d", returnFormRepository.count() + 1);
    }
}
