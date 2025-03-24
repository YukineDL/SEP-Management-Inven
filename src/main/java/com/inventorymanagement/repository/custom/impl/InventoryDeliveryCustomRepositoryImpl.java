package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.dto.InventoryDeliveryDTO;
import com.inventorymanagement.dto.InventoryDeliverySearchReqDTO;
import com.inventorymanagement.repository.custom.InventoryDeliveryCustomRepository;
import com.inventorymanagement.utils.RepositoryUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InventoryDeliveryCustomRepositoryImpl implements InventoryDeliveryCustomRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<InventoryDeliveryDTO> findBySearchRequest(Pageable pageable, InventoryDeliverySearchReqDTO reqDTO) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder sqlSelect = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();
        StringBuilder orderBySql = new StringBuilder();
        StringBuilder pageSql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        sqlSelect.append("""
                Select id.code, id.customer_id, id.approve_status, id.approve_by, id.approve_date, id.tax_number,
                id.total_amount, id.tax_export_gtgt, id.create_at, id.employee_code
                """);
        whereSql.append("""
                from inventory_delivery id
                where 1=1
                """);
        orderBySql.append("""
                order by id.create_at desc
                """);
        pageSql.append(" limit ")
                .append(pageable
                        .getPageSize())
                .append(" offset ")
                .append(pageable.getOffset());
        addParams(whereSql,params,reqDTO);
        sql.append(sqlSelect)
                .append(whereSql)
                .append(orderBySql)
                .append(pageSql);
        countSql.append("""
                SELECT COUNT(1)
                """).append(whereSql);
        Query count = em.createNativeQuery(countSql.toString());
        setParams(count,params);
        Query sqlQuery = em.createNativeQuery(sql.toString());
        setParams(sqlQuery,params);
        List<Object[]> objects = sqlQuery.getResultList();
        List<InventoryDeliveryDTO> results = new ArrayList<>();
        for (Object[] row : objects){
            AtomicInteger index = new AtomicInteger(0);
            InventoryDeliveryDTO item = new InventoryDeliveryDTO();
            item.setCode(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            item.setCustomerId(RepositoryUtils.setValue(row[index.getAndIncrement()], Integer.class));
            item.setApproveStatus(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            item.setApproveBy(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            item.setApproveDate(RepositoryUtils.setValue(row[index.getAndIncrement()], LocalDateTime.class));
            item.setTaxNumber(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            item.setTotalAmount(RepositoryUtils.setValue(row[index.getAndIncrement()], Double.class));
            item.setTaxExportGtGt((Float) RepositoryUtils.setValue(row[index.getAndIncrement()], Number.class));
            item.setCreateAt(RepositoryUtils.setValue(row[index.getAndIncrement()], LocalDateTime.class));
            item.setEmployeeCode(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            results.add(item);
        }
        return new PageImpl<>(results, pageable,(Long)count.getSingleResult());
    }
    private void addParams(StringBuilder whereClause, Map<String,Object> params, InventoryDeliverySearchReqDTO reqDTO){
        if(StringUtils.isNotEmpty(reqDTO.getCode())){
            whereClause.append(" and id.code like ")
                    .append("'%").append(reqDTO.getCode()).append("%'");
        }
        if(!Objects.isNull(reqDTO.getCustomerId())){
            whereClause.append("""
                    and id.customer_id = :customerId
                    """);
            params.put("customerId", reqDTO.getCustomerId());
        }
        if(!Objects.isNull(reqDTO.getApproveStatus())){
            whereClause.append("""
                    and id.approve_status = :approveStatus
                    """);
            params.put("approveStatus", reqDTO.getApproveStatus());
        }
        if(!Objects.isNull(reqDTO.getFromDate()) && !Objects.isNull(reqDTO.getToDate())){
            whereClause.append(" and ( id.create_at >= :fromDate and id.create_at <= :toDate ) ");
            params.put("fromDate",reqDTO.getFromDate());
            params.put("toDate",reqDTO.getToDate());
        }else if(!Objects.isNull(reqDTO.getFromDate())){
            whereClause.append(" and id.create_at >= :fromDate ");
            params.put("fromDate",reqDTO.getFromDate());
        }else if(!Objects.isNull(reqDTO.getToDate())){
            whereClause.append(" and id.create_at <= :toDate ");
            params.put("toDate",reqDTO.getToDate());
        }
        if(!Objects.isNull(reqDTO.getTotalAmountTo())){
            whereClause.append(" and id.total_amount <= :totalAmountTo ");
            params.put("totalAmountTo", reqDTO.getTotalAmountTo());
        }
        if(!Objects.isNull(reqDTO.getTotalAmountFrom())){
            whereClause.append(" and id.total_amount >= :totalAmountFrom ");
            params.put("totalAmountFrom", reqDTO.getTotalAmountFrom());
        }
    }
    private void setParams(Query query, Map<String, Object> params){
        params.forEach(query::setParameter);
    }
}
