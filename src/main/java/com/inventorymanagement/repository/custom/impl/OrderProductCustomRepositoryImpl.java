package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.dto.ProductOrderDTO;
import com.inventorymanagement.repository.custom.OrderProductCustomRepository;
import com.inventorymanagement.utils.RepositoryUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class OrderProductCustomRepositoryImpl implements OrderProductCustomRepository {
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<ProductOrderDTO> findByOrderCode(String orderCode) {
        StringBuilder sql = new StringBuilder();
        StringBuilder groupBySql = new StringBuilder();
        String selectSQL = """
                select p.code , p.name, p.unit , op.quantity,
                CAST(SUM(COALESCE(bn.inventory_quantity, 0)) AS SIGNED) AS inventory_quantity,
                p.selling_price, op.discount
                from `order` o
                JOIN order_product op on op.order_code = o.code
                JOIN product p on p.code = op.product_code
                LEFT JOIN batch_number bn on bn.product_code = op.product_code
                where 1=1
                """;
        groupBySql.append("""
                group by op.product_code, op.quantity, p.name, p.unit, p.selling_price, op.discount
                """);
        StringBuilder whereSQL = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        addingWhereClause(params, whereSQL, orderCode);
        sql.append(selectSQL)
                .append(whereSQL).append(groupBySql);
        Query query = em.createNativeQuery(sql.toString());
        setParams(params, query);
        List<Objects[]> objects = query.getResultList();
        List<ProductOrderDTO> productOrderDTOS = new ArrayList<>();
        for(Object[] object : objects){
            AtomicInteger index = new AtomicInteger(0);
            ProductOrderDTO dto = new ProductOrderDTO();
            dto.setCode(RepositoryUtils.setValue(object[index.getAndIncrement()], String.class));
            dto.setName(RepositoryUtils.setValue(object[index.getAndIncrement()], String.class));
            dto.setUnit(RepositoryUtils.setValue(object[index.getAndIncrement()], String.class));
            dto.setQuantity(RepositoryUtils.setValue(object[index.getAndIncrement()], Integer.class));
            dto.setInventoryQuantity(RepositoryUtils.setValue(object[index.getAndIncrement()], Long.class));
            dto.setSellingPrice(RepositoryUtils.setValue(object[index.getAndIncrement()], Double.class));
            dto.setDiscount((Float) RepositoryUtils.setValue(object[index.getAndIncrement()], Number.class));
            productOrderDTOS.add(dto);
        }
        return productOrderDTOS;
    }
    private void addingWhereClause(Map<String, Object> parameters, StringBuilder sql, String orderCode) {
        sql.append(" and op.order_code = :orderCode ");
        parameters.put("orderCode", orderCode);
    }
    private void setParams(Map<String, Object> params, Query query) {
        params.forEach(query::setParameter);
    }
}
