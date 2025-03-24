package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.*;
import com.inventorymanagement.dto.*;
import com.inventorymanagement.entity.*;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.*;
import com.inventorymanagement.repository.custom.InventoryDeliveryCustomRepository;
import com.inventorymanagement.repository.custom.impl.InventoryDeliveryCustomRepositoryImpl;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.services.IInventoryDeliveryServices;
import com.inventorymanagement.services.IOrderServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
        List<BatchNumber> inventoryProducts = batchNumberRepository.findByProductCodeInAndStatusOrderByCreateAtAsc(orderProductCodes,
                PRODUCT_STATUS.NEW.name());
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
        inventoryDeliveryRepository.save(inventoryDelivery);
        orderRepository.save(order);
        String key = UUID.randomUUID().toString();
        ProcessCheck process = ProcessCheck.builder()
                .checkSync(key)
                .status(Constants.PROCESSING)
                .createAt(LocalDateTime.now())
                .build();
        processCheckRepository.save(process);
        CompletableFuture.runAsync(() -> {
            // handle export product
           try {
               List<BatchNumber> listBatchNumberChange = new ArrayList<>();
               for (ProductOrderDTO product : orderDTO.getOrderProducts()){
                   int totalQuantity = product.getQuantity();
                   var batchList = productInventoryMap.get(product.getCode()).stream()
                           .filter(item -> item.getInventoryQuantity() > 0).toList();
                   for (BatchNumber batch : batchList){
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
               batchNumberRepository.saveAll(listBatchNumberChange);
               List<ProductDelivery> itemExportDelivery = new ArrayList<>();
               for (BatchNumber batch : listBatchNumberChange){
                   var discount = productWithDiscount.get(batch.getProductCode());
                   var product = mapProduct.get(batch.getProductCode());
                   var priceExport = (product.getSellingPrice() * batch.getExportQuantity()) - (product.getSellingPrice() * batch.getExportQuantity() * discount) ;
                   ProductDelivery item = ProductDelivery.builder()
                           .inventoryDeliveryCode(inventoryDelivery.getCode())
                           .batchNumberId(batch.getId())
                           .exportQuantity(batch.getExportQuantity() - batch.getExportQuantityLast())
                           .priceExport(Math.ceil(priceExport))
                           .build();
                   itemExportDelivery.add(item);
               }
               productDeliveryRepository.saveAll(itemExportDelivery);
               process.setStatus(Constants.SUCCESS);
               processCheckRepository.save(process);
           } catch (Exception e){
                process.setStatus(Constants.FAIL);
                processCheckRepository.save(process);
                log.info(e.getMessage());
           }
        });
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
    public Page<InventoryDeliveryDTO> findBySearchRequest(InventoryDeliverySearchReqDTO reqDTO, Pageable pageable) {
        Page<InventoryDeliveryDTO> content = inventoryDeliveryCustomRepository.findBySearchRequest(pageable,reqDTO);
        Map<Integer, Customer> customerMap = customerRepository.findAll().stream().collect(
                Collectors.toMap(Customer::getId, customer -> customer)
        );
        var productsMap = productRepository.findAll().stream().collect(
                Collectors.toMap(Product::getCode,product -> product)
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
        var productsMap = productRepository.findAll().stream().collect(
                Collectors.toMap(Product::getCode,product -> product)
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

    private String createCode(){
        return Constants.INVENTORY_DELIVERY_CODE +
                String.format("%05d", inventoryDeliveryRepository.count() + 1);
    }
}
