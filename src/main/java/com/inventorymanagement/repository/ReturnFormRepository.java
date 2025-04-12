package com.inventorymanagement.repository;

import com.inventorymanagement.entity.ReturnForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnFormRepository extends JpaRepository<ReturnForm, Integer> {
    Optional<ReturnForm> findByCode(String code);
    List<ReturnForm> findByApproveStatusInAndOrderCode(List<String> status,String orderCode);
}
