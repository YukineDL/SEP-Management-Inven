package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.dto.InventoryReceiptSearchReq;
import com.inventorymanagement.entity.InventoryReceipt;
import com.inventorymanagement.repository.custom.InventoryReceiptCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class InventoryReceiptCustomRepositoryImpl implements InventoryReceiptCustomRepository {
    @PersistenceContext
    private EntityManager em;
    @Override
    public Page<InventoryReceipt> findBySearchRequest(InventoryReceiptSearchReq req, Pageable pageable) {
        StringBuilder sql = new StringBuilder();
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("SELECT * ");
        StringBuilder whereSql = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        whereSql.append("""
                from inventory_receipt ir
                where 1=1
                """);
        addingParams(params, whereSql, req);
        StringBuilder orderSql = new StringBuilder();
        orderSql.append(" order by ir.create_at_date_time desc ");
        StringBuilder pageSql = new StringBuilder();
        pageSql.append(" limit ").append(pageable.getPageSize())
                .append(" offset ")
                .append(pageable.getOffset());
        sql.append(selectSql)
                .append(whereSql)
                .append(orderSql)
                .append(pageSql);

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(1) ").append(whereSql);
        Query countQuery = em.createNativeQuery(countSql.toString());
        Query query = em.createNativeQuery(sql.toString(), InventoryReceipt.class);
        setParams(params, query);
        setParams(params, countQuery);
        return new PageImpl<>(query.getResultList(), pageable, (Long)countQuery.getSingleResult());
    }
    private void addingParams(Map<String, Object> params, StringBuilder sqlWhere, InventoryReceiptSearchReq req) {
        if(StringUtils.isNotEmpty(req.getEmployeeCode())){
            sqlWhere.append(" and ir.employee_code = :employeeCode ");
            params.put("employeeCode", req.getEmployeeCode());
        }
        if(StringUtils.isNotEmpty(req.getPurchaseOrderCode())){
            sqlWhere.append(" and ir.purchase_order_code = :purchaseOrderCode ");
            params.put("purchaseOrderCode", req.getPurchaseOrderCode());
        }
        if(StringUtils.isNotEmpty(req.getApprove())){
            sqlWhere.append(" and ir.approve = :approve ");
            params.put("approve", req.getApprove());
        }
        if(StringUtils.isNotEmpty(req.getNumberOfReceipts())){
            sqlWhere.append(" and ir.number_of_receipts = :numberOfReceipts ");
            params.put("numberOfReceipts", req.getNumberOfReceipts());
        }
        if(StringUtils.isNotEmpty(req.getCode())){
            sqlWhere.append(" and ir.code like ")
                    .append("'%").append(req.getCode()).append("%'");
        }
        if(!Objects.isNull(req.getSupplierId())){
            sqlWhere.append(" and ir.supplier_id = :supplierId ");
            params.put("supplierId", req.getSupplierId());
        }
        if(!Objects.isNull(req.getFromDate()) && !Objects.isNull(req.getToDate())){
            sqlWhere.append(" and ( ir.create_at >= :fromDate and ir.create_at <= :toDate ) ");
            params.put("fromDate",req.getFromDate());
            params.put("toDate",req.getToDate());
        }else if(!Objects.isNull(req.getFromDate())){
            sqlWhere.append(" and ( ir.create_at >= :fromDate and ir.create_at <= CURRENT_DATE )");
            params.put("fromDate",req.getFromDate());
        }else if(!Objects.isNull(req.getToDate())){
            sqlWhere.append(" and ir.create_at <= :toDate");
            params.put("toDate",req.getToDate());
        }
    }
    private void setParams(Map<String, Object> params, Query sql) {
        params.forEach(sql::setParameter);
    }
}
