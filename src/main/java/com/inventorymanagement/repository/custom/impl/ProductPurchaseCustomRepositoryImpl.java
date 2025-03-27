package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.dto.ProductPurchaseDTO;
import com.inventorymanagement.repository.custom.ProductPurchaseCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductPurchaseCustomRepositoryImpl implements ProductPurchaseCustomRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ProductPurchaseDTO> findByPurchaseOrderCode(String purchaseOrderCode) {
        StringBuilder sql = new StringBuilder();
        String selectSQL = """
                select p.code, p.name, p.unit, ppo.quantity
                from product_purchase_order ppo
                join product p on p.code = ppo.product_code
                where 1=1
                """;
        StringBuilder whereSQL = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        addingWhereClause(params, whereSQL, purchaseOrderCode);
        sql.append(selectSQL)
           .append(whereSQL);
        Query query = em.createNativeQuery(sql.toString(), ProductPurchaseDTO.class);
        setParams(params, query);
        return query.getResultList();
    }
    private void addingWhereClause(Map<String, Object> parameters, StringBuilder sql, String purchaseOrderCode) {
        sql.append(" and ppo.purchase_order_code = :purchaseOrderCode");
        parameters.put("purchaseOrderCode", purchaseOrderCode);
    }
    private void setParams(Map<String, Object> params, Query query) {
        params.forEach(query::setParameter);
    }
}
