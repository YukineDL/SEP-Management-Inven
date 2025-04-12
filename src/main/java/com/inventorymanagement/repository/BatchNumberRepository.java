package com.inventorymanagement.repository;

import com.inventorymanagement.entity.BatchNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BatchNumberRepository extends JpaRepository<BatchNumber, Integer> {
    List<BatchNumber> findByInventoryReceiptCode(String code);
    @Query(value = """
    SELECT * 
    FROM batch_number b
    WHERE b.status IN :status
    AND b.product_code IN :productCodes
    ORDER BY b.date_expired
""", nativeQuery = true)
    List<BatchNumber> findProductByStatusInAndProductCodeIn(List<String> productCodes, List<String> status);

    List<BatchNumber> findByIdIn(List<Integer> ids);
}
