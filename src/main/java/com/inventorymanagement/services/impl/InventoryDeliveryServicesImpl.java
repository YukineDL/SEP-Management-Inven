package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.*;
import com.inventorymanagement.dto.*;
import com.inventorymanagement.entity.*;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.*;
import com.inventorymanagement.repository.custom.InventoryDeliveryCustomRepository;
import com.inventorymanagement.repository.custom.ProductDeliveryCustomRepository;
import com.inventorymanagement.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryDeliveryServicesImpl implements IInventoryDeliveryServices {
    private final IEmployeeServices employeeServices;
    private final OrderProductRepository orderProductRepository;
    private final BatchNumberRepository batchNumberRepository;
    private final IOrderServices orderServices;
    private final InventoryDeliveryRepository inventoryDeliveryRepository;
    private final ProductDeliveryRepository productDeliveryRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryDeliveryCustomRepository inventoryDeliveryCustomRepository;
    private final CustomerRepository customerRepository;
    private final ProductDeliveryCustomRepository productDeliveryCustomRepository;
    private final EmployeeRepository employeeRepository;
    private final ProcessCheckRepository processCheckRepository;
    private final IReturnFormServices returnFormServices;
    private final UnitRepository unitRepository;
    private final IProductServices productServices;
    private final ReturnFormRepository returnFormRepository;

    @Override
    public void createInventoryDeliveryByOrderCode(String authHeader, String orderCode, InventoryDeliveryCreateDTO dto) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(me == null || me.getRoleCode().equals(RoleEnum.SALE.name())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        // get information is used
        var orderOP = orderRepository.findByCode(orderCode);
        if(orderOP.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.ORDER_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.ORDER_NOT_EXISTED)
            );
        }
        Order order = orderOP.get();
        if(BooleanUtils.isTrue(order.getIsUsed())){
            throw new InventoryException(
                    ExceptionMessage.ORDER_IS_USED,
                    ExceptionMessage.messages.get(ExceptionMessage.ORDER_IS_USED)
            );
        }
        order.setIsUsed(true);
        List<String> statusApprove = List.of(PURCHASE_ORDER_APPROVE.WAITING.name(),PURCHASE_ORDER_APPROVE.REJECTED.name());
        if(statusApprove.contains(order.getApproveStatus())){
            throw new InventoryException(
                    ExceptionMessage.ORDER_APPROVE_STATUS_INVALID,
                    ExceptionMessage.messages.get(ExceptionMessage.ORDER_APPROVE_STATUS_INVALID)
            );
        }
        // get product in order
        List<OrderProduct> orderProducts = orderProductRepository.findByOrderCode(orderCode);
        List<String> orderProductCodes = orderProducts.stream().map(OrderProduct::getProductCode).toList();
        List<BatchNumber> inventoryProducts = batchNumberRepository.findProductByStatusInAndProductCodeIn(orderProductCodes,
                List.of(PRODUCT_STATUS.NEW.name(),PRODUCT_STATUS.OLD.name()));
        Map<String, List<BatchNumber>> productInventoryMap = inventoryProducts.stream().collect(
                Collectors.groupingBy(BatchNumber::getProductCode)
        );
        // get all product to map
        Map<String, Product> mapProduct = productRepository.findAll().stream().collect(
                Collectors.toMap(Product::getCode,product -> product)
        );
        // checking in inventory is enough quantity product to export
        OrderDTO orderDTO = orderServices.findOrderByCode(orderCode);
        Map<String, Float> productWithDiscount = orderDTO.getOrderProducts().stream().collect(
                Collectors.toMap(ProductOrderDTO::getCode,ProductOrderDTO::getDiscount)
        );
        for (ProductOrderDTO item : orderDTO.getOrderProducts()){
            if(item.getInventoryQuantity() < item.getQuantity()){
                throw new InventoryException(
                        ExceptionMessage.PRODUCT_NOT_ENOUGH,
                        String.format(ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_NOT_ENOUGH),item.getCode())
                );
            }
        }
        InventoryDelivery inventoryDelivery = InventoryDelivery.builder()
                .code(createCode())
                .deliveryType(DELIVERY_TYPE.DELIVERY_ORDER.name())
                .approveStatus(PURCHASE_ORDER_APPROVE.WAITING.name())
                .exportStatus(EXPORT_STATUS.WAITING_EXPORT.name())
                .createAt(LocalDateTime.now())
                .taxNumber(dto.getTaxNumber())
                .customerId(dto.getCustomerId())
                .taxExportGtGt(dto.getTaxExportGTGT())
                .totalAmount(dto.getTotalAmount())
                .orderCode(orderCode)
                .employeeCode(me.getCode())
                .build();
        // handle export product
        List<BatchNumber> listBatchNumberChange = new ArrayList<>();
        for (ProductOrderDTO product : orderDTO.getOrderProducts()){
            int totalQuantity = product.getQuantity();
            var batchList = productInventoryMap.get(product.getCode()).stream()
                    .filter(item -> item.getInventoryQuantity() > 0).toList();
            handleBatchNumber(batchList, listBatchNumberChange, totalQuantity);
        }
        batchNumberRepository.saveAll(listBatchNumberChange);
        List<ProductDelivery> itemExportDelivery = new ArrayList<>();
        for (BatchNumber batch : listBatchNumberChange){
            var exportQuantity = batch.getExportQuantity() - batch.getExportQuantityLast();
            var discount = productWithDiscount.get(batch.getProductCode());
            var product = mapProduct.get(batch.getProductCode());
            var priceExport = (product.getSellingPrice() * exportQuantity) - (product.getSellingPrice() * exportQuantity * discount) ;
            ProductDelivery item = ProductDelivery.builder()
                    .inventoryDeliveryCode(inventoryDelivery.getCode())
                    .batchNumberId(batch.getId())
                    .exportQuantity(exportQuantity)
                    .priceExport(Math.ceil(priceExport))
                    .build();
            itemExportDelivery.add(item);
        }
        inventoryDeliveryRepository.save(inventoryDelivery);
        orderRepository.save(order);
        productDeliveryRepository.saveAll(itemExportDelivery);
    }

    @Override
    public void approveInventoryDelivery(String authHeader, String inventoryDeliveryCode) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(me == null || !Constants.LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        var inventoryDeliverOp = inventoryDeliveryRepository.findByCode(inventoryDeliveryCode);
        if(inventoryDeliverOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.INVENTORY_DELIVERY_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.INVENTORY_DELIVERY_NOT_EXISTED)
            );
        }
        InventoryDelivery inventoryDelivery = inventoryDeliverOp.get();
        inventoryDelivery.setApproveStatus(PURCHASE_ORDER_APPROVE.APPROVED.name());
        inventoryDelivery.setApproveBy(me.getName());
        inventoryDelivery.setApproveDate(LocalDateTime.now());
        inventoryDeliveryRepository.save(inventoryDelivery);
    }

    @Override
    public void rejectInventoryDelivery(String authHeader, String inventoryDeliveryCode) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(me == null || !Constants.LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        var inventoryDeliverOp = inventoryDeliveryRepository.findByCode(inventoryDeliveryCode);
        if(inventoryDeliverOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.INVENTORY_DELIVERY_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.INVENTORY_DELIVERY_NOT_EXISTED)
            );
        }
        InventoryDelivery inventoryDelivery = inventoryDeliverOp.get();
        if(StringUtils.isNotEmpty(inventoryDelivery.getOrderCode())){
            var orderOp = orderRepository.findByCode(inventoryDelivery.getOrderCode());
            if(orderOp.isEmpty()){
                throw new InventoryException(
                        ExceptionMessage.ORDER_NOT_EXISTED,
                        ExceptionMessage.messages.get(ExceptionMessage.ORDER_NOT_EXISTED)
                );
            }
            var order = orderOp.get();
            order.setIsUsed(false);
            orderRepository.save(order);
        }
        inventoryDelivery.setApproveStatus(PURCHASE_ORDER_APPROVE.REJECTED.name());
        inventoryDelivery.setApproveBy(me.getName());
        inventoryDelivery.setApproveDate(LocalDateTime.now());
        inventoryDeliveryRepository.save(inventoryDelivery);
        // handle return product in delivery ticket to inventory
        List<ProductDelivery> productDeliveries = productDeliveryRepository.findByInventoryDeliveryCode(inventoryDeliveryCode);
        List<Integer> idsBatchNumber = productDeliveries.stream().map(ProductDelivery::getBatchNumberId).toList();
        Map<Integer, Integer> mapExportQuantity = productDeliveries.stream().collect(Collectors.toMap(ProductDelivery::getBatchNumberId,ProductDelivery::getExportQuantity));
        List<BatchNumber> batchNumbers = batchNumberRepository.findByIdIn(idsBatchNumber);
        for (BatchNumber item : batchNumbers){
            var exportQuantityInDelivery = mapExportQuantity.get(item.getId());
            item.setExportQuantity(item.getExportQuantity() - exportQuantityInDelivery);
            item.setInventoryQuantity(item.getInventoryQuantity() + exportQuantityInDelivery);
        }
        batchNumberRepository.saveAll(batchNumbers);
    }

    @Override
    public Page<InventoryDeliveryDTO> findBySearchRequest(InventoryDeliverySearchReqDTO reqDTO, Pageable pageable) throws InventoryException {
        Page<InventoryDeliveryDTO> content = inventoryDeliveryCustomRepository.findBySearchRequest(pageable,reqDTO);
        Map<Integer, Customer> customerMap = customerRepository.findAll().stream().collect(
                Collectors.toMap(Customer::getId, customer -> customer)
        );
        var productsMap = productServices.findAllBySearchRequest(
                null,PageRequest.of(0,Integer.MAX_VALUE)
        ).stream().collect(
                Collectors.toMap(ProductDTO::getCode,product -> product)
        );
        for (InventoryDeliveryDTO item : content.getContent()){
            item.setCustomer(customerMap.get(item.getCustomerId()));
            item.setProducts(productDeliveryCustomRepository.findByInventoryDeliveryCode(item.getCode()));
            for (ProductDeliveryDTO product : item.getProducts()){
                product.setProduct(productsMap.get(product.getProductCode()));
            }
        }
        return content;
    }

    @Override
    public InventoryDeliveryDTO findByCode(String inventoryDeliveryCode) throws InventoryException {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        InventoryDeliverySearchReqDTO searchReqDTO = InventoryDeliverySearchReqDTO.builder()
                .code(inventoryDeliveryCode)
                .build();
        var content = inventoryDeliveryCustomRepository.findBySearchRequest(pageable,searchReqDTO);
        if(content.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.INVENTORY_DELIVERY_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.INVENTORY_DELIVERY_NOT_EXISTED)
            );
        }
        var inventoryDelivery = content.getContent().get(0);
        var products = productDeliveryCustomRepository.findByInventoryDeliveryCode(inventoryDeliveryCode);
        inventoryDelivery.setProducts(products);
        var customers = customerRepository.findAll().stream().collect(
                Collectors.toMap(Customer::getId,customer -> customer)
        );
        var productsMap = productServices.findAllBySearchRequest(
                null,PageRequest.of(0,Integer.MAX_VALUE)
        ).stream().collect(
                Collectors.toMap(ProductDTO::getCode,product -> product)
        );
        var employeeMaps = employeeRepository.findAll().stream().collect(
                Collectors.toMap(Employee::getCode,e -> e)
        );
        for (ProductDeliveryDTO item : inventoryDelivery.getProducts()){
            item.setProduct(productsMap.get(item.getProductCode()));
        }
        inventoryDelivery.setCustomer(customers.get(inventoryDelivery.getCustomerId()));
        if(StringUtils.isNotEmpty(inventoryDelivery.getEmployeeCode())){
            inventoryDelivery.setEmployee(employeeMaps.get(inventoryDelivery.getEmployeeCode()));
        }
        return inventoryDelivery;
    }

    @Override
    public void createInventoryDeliveryReturn(String authHeader, String returnCode) throws InventoryException {
        Employee me = employeeServices.getFullInformation(authHeader);
        if(me == null || me.getRoleCode().equals(RoleEnum.SALE.name())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        var returnForm = this.returnFormServices.findReturnForm(returnCode);
        if(BooleanUtils.isTrue(returnForm.getReturnForm().getIsExport())){
            throw new InventoryException(
                    ExceptionMessage.RETURN_FORM_IS_USED,
                    ExceptionMessage.messages.get(ExceptionMessage.RETURN_FORM_IS_USED)
            );
        }
        if(returnForm.getReturnForm().getApproveStatus().equals(PURCHASE_ORDER_APPROVE.REJECTED.name()) ||
            returnForm.getReturnForm().getApproveStatus().equals(PURCHASE_ORDER_APPROVE.WAITING.name())){
            throw new InventoryException(
                    ExceptionMessage.INVENTORY_RECEIPT_NOT_APPROVE,
                    ExceptionMessage.messages.get(ExceptionMessage.INVENTORY_RECEIPT_NOT_APPROVE)
            );
        }
        returnForm.getReturnForm().setIsExport(true);
        returnFormRepository.save(returnForm.getReturnForm());
        // get product return broken
        var productReturnBroken = returnForm.getReturnProducts().stream().filter(item -> item.getStatusProduct().equals(PRODUCT_STATUS.BROKEN.name())).toList();
        if(productReturnBroken.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.RETURN_PRODUCT_BROKEN,
                    ExceptionMessage.messages.get(ExceptionMessage.RETURN_PRODUCT_BROKEN)
            );
        }
        // get codes
        var productCodesReturn = productReturnBroken.stream().map(ReturnProductDTO::getProductCode).toList();
        // get product inventory and filter quantity > 0
        var productInventory = batchNumberRepository.findProductByStatusInAndProductCodeIn(productCodesReturn,
                List.of(PRODUCT_STATUS.NEW.name(), PRODUCT_STATUS.OLD.name())).stream().filter(item -> item.getInventoryQuantity() > 0).toList();
        // sum data product to get quantity
        var productQuantity = productInventory.stream().collect(
                Collectors.toMap(
                        BatchNumber::getProductCode,
                        BatchNumber::getInventoryQuantity,
                        Integer::sum
                )
        );
        for (ReturnProductDTO item : productReturnBroken){
            var productInventoryQuantity = productQuantity.get(item.getProductCode());
            if(Objects.isNull(productInventoryQuantity)){
                throw new InventoryException(
                        ExceptionMessage.PRODUCT_NOT_ENOUGH,
                        String.format(
                                ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_NOT_ENOUGH),
                                item.getProductCode()
                        )
                );
            }
            if(item.getQuantityReturn() > productInventoryQuantity){
                throw new InventoryException(
                        ExceptionMessage.PRODUCT_NOT_ENOUGH,
                        String.format(
                                ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_NOT_ENOUGH),
                                item.getProductCode()
                        )
                );
            }
        }
        List<BatchNumber> listBatchNumberChange = new ArrayList<>();
        for (ReturnProductDTO product : productReturnBroken){
            int totalQuantity = product.getQuantityReturn();
            handleBatchNumber(productInventory, listBatchNumberChange, totalQuantity);
        }
        InventoryDelivery inventoryDelivery = InventoryDelivery.builder()
                .code(createCode())
                .deliveryType(DELIVERY_TYPE.DELIVERY_RETURN.name())
                .approveStatus(PURCHASE_ORDER_APPROVE.WAITING.name())
                .exportStatus(EXPORT_STATUS.WAITING_EXPORT.name())
                .createAt(LocalDateTime.now())
                .customerId(returnForm.getCustomer().getId())
                .totalAmount((double) 0)
                .employeeCode(me.getCode())
                .returnFormCode(returnCode)
                .build();
        List<ProductDelivery> productDeliveryList = new ArrayList<>();
        for(BatchNumber batch : listBatchNumberChange){
            var exportQuantity = batch.getExportQuantity() - batch.getExportQuantityLast();
            ProductDelivery productDelivery = ProductDelivery.builder()
                    .batchNumberId(batch.getId())
                    .exportQuantity(exportQuantity)
                    .inventoryDeliveryCode(inventoryDelivery.getCode())
                    .priceExport((double)0)
                    .build();
            productDeliveryList.add(productDelivery);
        }
        inventoryDeliveryRepository.save(inventoryDelivery);
        batchNumberRepository.saveAll(listBatchNumberChange);
        productDeliveryRepository.saveAll(productDeliveryList);
    }

    private void handleBatchNumber(List<BatchNumber> productInventory, List<BatchNumber> listBatchNumberChange, int totalQuantity) {
        for (BatchNumber batch : productInventory){
            totalQuantity -= batch.getInventoryQuantity();
            // if total quantity customer order is clear enough with product in inventory save batch number and stop
            if(totalQuantity <= 0){
                batch.setInventoryQuantity(Math.abs(totalQuantity));
                int exportQuantity = batch.getQuantityShipped() - batch.getInventoryQuantity();
                batch.setExportQuantityLast(batch.getExportQuantity());
                batch.setExportQuantity(exportQuantity);
                listBatchNumberChange.add(batch);
                break;
            }else{
                batch.setInventoryQuantity(0);
                int exportQuantity = batch.getQuantityShipped() - batch.getInventoryQuantity();
                batch.setExportQuantityLast(batch.getExportQuantity());
                batch.setExportQuantity(exportQuantity);
                listBatchNumberChange.add(batch);
            }
        }
    }

    private String createCode(){
        return Constants.INVENTORY_DELIVERY_CODE +
                String.format("%05d", inventoryDeliveryRepository.count() + 1);
    }
}
