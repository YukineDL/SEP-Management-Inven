package com.inventorymanagement.repository;

import com.inventorymanagement.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Page<Supplier> findByIsDeleted(Boolean isDeleted, Pageable pageable);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumberAndPhoneNumberNot(String phoneNumber, String phoneNumberNot);
}
