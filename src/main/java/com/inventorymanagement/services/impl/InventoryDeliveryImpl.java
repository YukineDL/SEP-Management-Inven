package com.inventorymanagement.services.impl;
 
 import com.inventorymanagement.constant.PRODUCT_STATUS;
 import com.inventorymanagement.constant.RoleEnum;
 import com.inventorymanagement.dto.OrderDTO;
 import com.inventorymanagement.dto.ProductOrderDTO;
 import com.inventorymanagement.entity.BatchNumber;
 import com.inventorymanagement.entity.Employee;
 import com.inventorymanagement.entity.OrderProduct;
 import com.inventorymanagement.exception.ExceptionMessage;
 import com.inventorymanagement.exception.InventoryException;
 import com.inventorymanagement.repository.BatchNumberRepository;
 import com.inventorymanagement.repository.OrderProductRepository;
 import com.inventorymanagement.services.IEmployeeServices;
 import com.inventorymanagement.services.IInventoryDeliveryServices;
 import com.inventorymanagement.services.IOrderServices;
 import lombok.RequiredArgsConstructor;
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.springframework.stereotype.Service;
 
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.stream.Collectors;
 
 @Service
 @RequiredArgsConstructor
 public class InventoryDeliveryImpl implements IInventoryDeliveryServices {
     private final IEmployeeServices employeeServices;
     private final OrderProductRepository orderProductRepository;
     private final BatchNumberRepository batchNumberRepository;
     private final IOrderServices orderServices;
     @Override
     public void createInventoryDeliveryByOrderCode(String authHeader, String orderCode) {
 
     public void createInventoryDeliveryByOrderCode(String authHeader, String orderCode) throws InventoryException {
         Employee me = employeeServices.getFullInformation(authHeader);
         if(me == null || me.getRoleCode().equals(RoleEnum.SALE.name())){
             throw new InventoryException(
                     ExceptionMessage.NO_PERMISSION,
                     ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
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
         // checking in inventory is enough quantity product to export
         OrderDTO orderDTO = orderServices.findOrderByCode(orderCode);
         for (ProductOrderDTO item : orderDTO.getOrderProducts()){
             if(item.getInventoryQuantity() < item.getQuantity()){
                 throw new InventoryException(
                         ExceptionMessage.PRODUCT_NOT_ENOUGH,
                         String.format(ExceptionMessage.messages.get(ExceptionMessage.PRODUCT_NOT_ENOUGH),item.getCode())
                 );
             }
         }
         List<BatchNumber> listBatchNumberChange = new ArrayList<>();
         for (ProductOrderDTO product : orderDTO.getOrderProducts()){
             int totalQuantity = product.getQuantity();
             for (BatchNumber batch : productInventoryMap.get(product.getCode())){
                 totalQuantity -= batch.getInventoryQuantity();
                 // if total quantity customer order is clear enough with product in inventory save batch number and stop
                 if(totalQuantity <= 0){
                     batch.setInventoryQuantity(Math.abs(totalQuantity));
                     listBatchNumberChange.add(batch);
                     break;
                 }else{
                     batch.setInventoryQuantity(0);
                     listBatchNumberChange.add(batch);
                 }
             }
         }
     }
 }