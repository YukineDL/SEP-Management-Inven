package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.dto.ReturnFormSearchReq;
import com.inventorymanagement.entity.ReturnForm;
import com.inventorymanagement.repository.custom.ReturnFormCustomRepository;
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
public class ReturnFormCustomRepositoryImpl extends BaseCustomRepository implements ReturnFormCustomRepository  {
    @PersistenceContext
    private EntityManager em;
    @Override
    public Page<ReturnForm> findBySearchReq(ReturnFormSearchReq req, Pageable pageable) {
        StringBuilder sql = new StringBuilder();
        StringBuilder selectSql = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();
        StringBuilder orderSql = new StringBuilder();
        StringBuilder pageSql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        selectSql.append("select * ");
        countSql.append("select count(*) ");
        whereSql.append(" from return_form rf" +
                        " where 1=1 ");
        orderSql.append(" order by rf.create_at desc ");
        this.addingPageQuery(pageSql,pageable);
        addParams(whereSql,req,params);
        sql.append(selectSql).append(whereSql).append(orderSql).append(pageSql);
        countSql.append(whereSql);
        Query query = em.createNativeQuery(sql.toString(), ReturnForm.class);
        this.setParams(query,params);
        Query countQuery = em.createNativeQuery(countSql.toString());
        this.setParams(countQuery,params);
        return new PageImpl<>(query.getResultList(), pageable, (Long)countQuery.getSingleResult());
    }
    private void addParams(StringBuilder sql, ReturnFormSearchReq req, Map<String, Object> params) {
        if(StringUtils.isNotEmpty(req.getCode())){
            sql.append(" and o.code like ")
                    .append("'%").append(req.getCode()).append("%'");
        }
        if(!Objects.isNull(req.getFromDate()) && !Objects.isNull(req.getToDate())){
            sql.append(" and ( rf.create_at >= :fromDate and rf.create_at <= :toDate ) ");
            params.put("fromDate",req.getFromDate());
            params.put("toDate",req.getToDate());
        }else if(!Objects.isNull(req.getFromDate())){
            sql.append(" and rf.create_at >= :fromDate ");
            params.put("fromDate",req.getFromDate());
        }else if(!Objects.isNull(req.getToDate())){
            sql.append(" and rf.create_at <= :toDate ");
            params.put("toDate",req.getToDate());
        }
        if(!Objects.isNull(req.getIsUsed())){
            sql.append(" and rf.is_used = :isUsed ");
            params.put("isUsed",req.getIsUsed());
        }
        if(!Objects.isNull(req.getCustomerId())){
            sql.append(" and rf.customer_id = :customerId ");
            params.put("customerId",req.getCustomerId());
        }
        if(!Objects.isNull(req.getAmountFrom())){
            sql.append(" and rf.total_amount >= :amountFrom ");
            params.put("amountFrom",req.getAmountFrom());
        }
        if(!Objects.isNull(req.getAmountTo())){
            sql.append(" and rf.total_amount <= :amountTo ");
            params.put("amountTo",req.getAmountTo());
        }
        if(StringUtils.isNotEmpty(req.getApproveStatus())){
            sql.append(" and rf.approve_status <= :approveStatus ");
            params.put("approveStatus",req.getApproveStatus());
        }
    }

}
