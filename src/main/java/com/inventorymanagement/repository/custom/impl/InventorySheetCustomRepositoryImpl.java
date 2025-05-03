package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.dto.InventoryReceiptSearchReq;
import com.inventorymanagement.dto.InventorySheetSearchDTO;
import com.inventorymanagement.entity.InventoryReceipt;
import com.inventorymanagement.entity.InventorySheet;
import com.inventorymanagement.repository.custom.InventoryReceiptCustomRepository;
import com.inventorymanagement.repository.custom.InventorySheetCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class InventorySheetCustomRepositoryImpl extends BaseCustomRepository implements InventorySheetCustomRepository {
    @PersistenceContext
    private EntityManager em;
    @Override
    public Page<InventorySheet> findBySearchReq(InventorySheetSearchDTO req, Pageable pageable) {
        StringBuilder sql = new StringBuilder();
        StringBuilder selectSql = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();
        StringBuilder pageSql = new StringBuilder();
        StringBuilder orderSql = new StringBuilder();
        selectSql.append("""
                SELECT *
                """);
        whereSql.append("""
                FROM inventory_sheet
                WHERE 1=1
                """);
        orderSql.append("""
                order by create_at desc
                """);
        var params = new HashMap<String, Object>();
        this.addParams(whereSql,params, req);
        sql.append(selectSql).append(whereSql).append(orderSql).append(pageSql);
        Query query = em.createNativeQuery(sql.toString(),InventorySheet.class);
        this.setParams(query,params);
        this.addingPageQuery(pageSql,pageable);
        return new PageImpl<>(query.getResultList(), pageable,query.getResultList().size());
    }
    private void addParams(StringBuilder sql, Map<String, Object> params, InventorySheetSearchDTO dto) {
        if(!Objects.isNull(dto.getStartDate()) && !Objects.isNull(dto.getEndDate())) {
            sql.append(" and start_date >= :startDate and end_date <= :endDate ");
            params.put("startDate", dto.getStartDate());
            params.put("endDate", dto.getEndDate());
        }else if(!Objects.isNull(dto.getStartDate())){
            sql.append(" and start_date >= :startDate ");
            params.put("startDate", dto.getStartDate());
        }else if(!Objects.isNull(dto.getEndDate())){
            sql.append(" and end_date <= :endDate ");
            params.put("endDate", dto.getEndDate());
        }
        if(!Objects.isNull(dto.getIsReview())){
            sql.append(" and is_review = :isReview ");
            params.put("isReview", dto.getIsReview());
        }
    }
}
