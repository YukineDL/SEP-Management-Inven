package com.inventorymanagement.repository;
import com.inventorymanagement.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
 public interface OrderProductRepository extends JpaRepository<OrderProduct, Integer> {
     void deleteByOrderCode(String orderCode);
     List<OrderProduct> findByOrderCode(String orderCode);
 }