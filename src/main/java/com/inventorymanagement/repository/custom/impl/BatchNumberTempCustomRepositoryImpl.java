package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.dto.BatchNumberDTO;
import com.inventorymanagement.repository.custom.BatchNumberTempCustomRepository;
import com.inventorymanagement.utils.RepositoryUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class BatchNumberTempCustomRepositoryImpl implements BatchNumberTempCustomRepository {
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<BatchNumberDTO> findBatchNumberTempByCode(String inventoryCode) {
        StringBuilder sql = new StringBuilder();
        StringBuilder selectSql = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();
        selectSql.append("""
                SELECT p.code, p.name, p.unit, bn.unit_price,
                bn.date_of_manufacture, bn.date_expired, bn.location, bn.inventory_quantity, bn.quantity_shipped
                """);
        whereSql.append("""
                FROM batch_number_temp bn
                JOIN product p ON p.code = bn.product_code
                WHERE bn.inventory_receipt_code = :inventoryCode
                """);
        sql.append(selectSql).append(whereSql);
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("inventoryCode", inventoryCode);
        List<Object[]> objects = query.getResultList();
        List<com.inventorymanagement.dto.BatchNumberDTO> dtos = new ArrayList<>();
        for (Object[] row : objects) {
            AtomicInteger index = new AtomicInteger(0);
            com.inventorymanagement.dto.BatchNumberDTO dto = new com.inventorymanagement.dto.BatchNumberDTO();
            dto.setProductCode(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            dto.setProductName(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            dto.setProductUnit(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            dto.setUnitPrice(RepositoryUtils.setValue(row[index.getAndIncrement()], Double.class));
            dto.setDateOfManufacture(RepositoryUtils.setValue(row[index.getAndIncrement()], LocalDate.class));
            dto.setDateExpired(RepositoryUtils.setValue(row[index.getAndIncrement()], LocalDate.class));
            dto.setLocation(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            dto.setInventoryQuantity(RepositoryUtils.setValue(row[index.getAndIncrement()], Integer.class));
            dto.setQuantityShipped(RepositoryUtils.setValue(row[index.getAndIncrement()], Integer.class));
            dtos.add(dto);
        }
        return dtos;
    }
}
